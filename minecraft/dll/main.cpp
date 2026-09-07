#include <windows.h>
#include <mmsystem.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define EXPORT extern "C" __declspec(dllexport)

static CRITICAL_SECTION g_stateLock;
static bool g_lockReady = false;
static volatile LONG g_effectsActive = 1;
static WORD g_originalGammaRamp[256 * 3];
static bool g_gammaBackupTaken = false;

static int clampInt(int value, int minimum, int maximum) {
    if (value < minimum) return minimum;
    if (value > maximum) return maximum;
    return value;
}

static DWORD endTime(DWORD duration) {
    return GetTickCount() + duration;
}

static bool active() {
    return InterlockedCompareExchange(&g_effectsActive, 0, 0) != 0;
}

static bool notExpired(DWORD end) {
    return active() && (LONG)(end - GetTickCount()) > 0;
}

static HWND findWindow(const char* title) {
    if (!title || !title[0]) return GetForegroundWindow();
    return FindWindowA(NULL, title);
}

static void pumpMessages() {
    MSG message;
    while (PeekMessage(&message, NULL, 0, 0, PM_REMOVE)) {
        TranslateMessage(&message);
        DispatchMessage(&message);
    }
}

static void sleepWithMessages(DWORD duration) {
    DWORD end = endTime(duration);
    while (notExpired(end)) {
        pumpMessages();
        Sleep(10);
    }
}

EXPORT void GDI_ScreenMelt(int duration, int intensity) {
    if (!active()) return;
    duration = (int)clampInt(duration, 1, 30000);
    intensity = clampInt(intensity, 1, 20);
    HDC screen = GetDC(NULL);
    if (!screen) return;

    int width = GetSystemMetrics(SM_CXSCREEN);
    int height = GetSystemMetrics(SM_CYSCREEN);
    DWORD end = endTime((DWORD)duration);
    while (notExpired(end)) {
        for (int i = 0; i < intensity && notExpired(end); ++i) {
            int x = rand() % width;
            int y = rand() % (height > 80 ? height - 80 : 1);
            int blockWidth = clampInt(30 + rand() % 120, 1, width - x);
            int offset = 4 + rand() % 24;
            int blockHeight = height - y - offset;
            if (blockHeight > 0) {
                BitBlt(screen, x, y + offset, blockWidth, blockHeight,
                    screen, x, y, SRCCOPY);
            }
        }
        Sleep(25);
    }
    ReleaseDC(NULL, screen);
}

EXPORT void GammaRampCorruptor(int mode, int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    mode = clampInt(mode, 0, 2);
    HDC screen = GetDC(NULL);
    if (!screen) return;

    EnterCriticalSection(&g_stateLock);
    if (!g_gammaBackupTaken) {
        g_gammaBackupTaken = GetDeviceGammaRamp(screen, g_originalGammaRamp) != FALSE;
    }
    WORD ramp[256 * 3];
    for (int i = 0; i < 256; ++i) {
        WORD value = (WORD)(i * 257);
        if (mode == 0) {
            ramp[i] = value;
            ramp[i + 256] = (WORD)(i * 55);
            ramp[i + 512] = (WORD)(i * 55);
        } else if (mode == 1) {
            WORD contrast = i < 128 ? 0 : 65535;
            ramp[i] = contrast;
            ramp[i + 256] = contrast;
            ramp[i + 512] = contrast;
        } else {
            ramp[i] = (WORD)(i * 180);
            ramp[i + 256] = (WORD)(i * 25);
            ramp[i + 512] = (WORD)(i * 25);
        }
    }
    SetDeviceGammaRamp(screen, ramp);
    DWORD end = endTime((DWORD)duration);
    while (notExpired(end)) Sleep(20);
    if (g_gammaBackupTaken) SetDeviceGammaRamp(screen, g_originalGammaRamp);
    LeaveCriticalSection(&g_stateLock);
    ReleaseDC(NULL, screen);
}

EXPORT void CursorPossession(int targetX, int targetY, int strength, int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    strength = clampInt(strength, 1, 100);
    int width = GetSystemMetrics(SM_CXSCREEN);
    int height = GetSystemMetrics(SM_CYSCREEN);
    targetX = clampInt(targetX, 0, width - 1);
    targetY = clampInt(targetY, 0, height - 1);
    DWORD end = endTime((DWORD)duration);
    while (notExpired(end)) {
        POINT cursor;
        if (GetCursorPos(&cursor)) {
            int moveX = cursor.x + (targetX - cursor.x) * strength / 100;
            int moveY = cursor.y + (targetY - cursor.y) * strength / 100;
            SetCursorPos(clampInt(moveX, 0, width - 1),
                clampInt(moveY, 0, height - 1));
        }
        Sleep(15);
    }
}

EXPORT void WindowPhysicalJitter(const char* windowTitle, int intensity, int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    intensity = clampInt(intensity, 1, 50);
    HWND window = findWindow(windowTitle);
    if (!window) return;
    RECT original;
    if (!GetWindowRect(window, &original)) return;
    DWORD end = endTime((DWORD)duration);
    while (notExpired(end) && IsWindow(window)) {
        int x = original.left + (rand() % (intensity * 2 + 1)) - intensity;
        int y = original.top + (rand() % (intensity * 2 + 1)) - intensity;
        SetWindowPos(window, NULL, x, y, 0, 0,
            SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE);
        Sleep(35);
    }
    if (IsWindow(window)) {
        SetWindowPos(window, NULL, original.left, original.top, 0, 0,
            SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE);
    }
}

EXPORT void SystemBeepHardware(int frequency, int duration) {
    if (!active()) return;
    Beep((DWORD)clampInt(frequency, 37, 32767),
        (DWORD)clampInt(duration, 1, 10000));
}

EXPORT void ClipboardWhisper(const char* text) {
    if (!active() || !text || !OpenClipboard(NULL)) return;
    EmptyClipboard();
    size_t length = strlen(text) + 1;
    HGLOBAL memory = GlobalAlloc(GMEM_MOVEABLE, length);
    if (memory) {
        void* target = GlobalLock(memory);
        if (target) {
            memcpy(target, text, length);
            GlobalUnlock(memory);
            if (!SetClipboardData(CF_TEXT, memory)) GlobalFree(memory);
        } else {
            GlobalFree(memory);
        }
    }
    CloseClipboard();
}

static LRESULT CALLBACK overlayProc(HWND window, UINT message,
    WPARAM wParam, LPARAM lParam) {
    if (message == WM_PAINT) {
        PAINTSTRUCT paint;
        HDC dc = BeginPaint(window, &paint);
        RECT rect;
        GetClientRect(window, &rect);
        HPEN pen = CreatePen(PS_SOLID, 2, RGB(180, 0, 0));
        HGDIOBJ old = SelectObject(dc, pen);
        for (int i = 0; i < 12; ++i) {
            MoveToEx(dc, rand() % rect.right, rand() % rect.bottom, NULL);
            LineTo(dc, rand() % rect.right, rand() % rect.bottom);
        }
        SelectObject(dc, old);
        DeleteObject(pen);
        EndPaint(window, &paint);
        return 0;
    }
    return DefWindowProc(window, message, wParam, lParam);
}

static HWND createOverlay(const char* className, WNDPROC procedure,
    COLORREF color, BYTE alpha) {
    HINSTANCE instance = GetModuleHandle(NULL);
    WNDCLASSEXA wc;
    ZeroMemory(&wc, sizeof(wc));
    wc.cbSize = sizeof(wc);
    wc.lpfnWndProc = procedure;
    wc.hInstance = instance;
    wc.hCursor = LoadCursor(NULL, IDC_ARROW);
    wc.lpszClassName = className;
    wc.hbrBackground = CreateSolidBrush(color);
    RegisterClassExA(&wc);
    HWND window = CreateWindowExA(WS_EX_LAYERED | WS_EX_TRANSPARENT |
        WS_EX_TOPMOST | WS_EX_NOACTIVATE, className, "", WS_POPUP, 0, 0,
        GetSystemMetrics(SM_CXSCREEN), GetSystemMetrics(SM_CYSCREEN),
        NULL, NULL, instance, NULL);
    if (window) {
        SetLayeredWindowAttributes(window, 0, alpha, LWA_ALPHA);
        ShowWindow(window, SW_SHOW);
        UpdateWindow(window);
    }
    return window;
}

EXPORT void DesktopOverlayGhost(int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    HWND window = createOverlay("UnknownGhostOverlay", overlayProc,
        RGB(0, 0, 0), 128);
    if (!window) return;
    sleepWithMessages((DWORD)duration);
    DestroyWindow(window);
    UnregisterClassA("UnknownGhostOverlay", GetModuleHandle(NULL));
}

EXPORT void TaskbarAggression(const char* windowTitle, int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    HWND window = findWindow(windowTitle);
    if (!window) return;
    FLASHWINFO info;
    ZeroMemory(&info, sizeof(info));
    info.cbSize = sizeof(info);
    info.hwnd = window;
    info.dwFlags = FLASHW_ALL | FLASHW_TIMERNOFG;
    info.uCount = (UINT)clampInt(duration / 500, 1, 60);
    info.dwTimeout = 500;
    FlashWindowEx(&info);
    sleepWithMessages((DWORD)duration);
    info.dwFlags = FLASHW_STOP;
    info.uCount = 0;
    FlashWindowEx(&info);
}

EXPORT void SetEffectsEnabled(bool enabled) {
    InterlockedExchange(&g_effectsActive, enabled ? 1 : 0);
    if (!enabled) {
        HDC screen = GetDC(NULL);
        if (screen) {
            EnterCriticalSection(&g_stateLock);
            if (g_gammaBackupTaken) SetDeviceGammaRamp(screen, g_originalGammaRamp);
            LeaveCriticalSection(&g_stateLock);
            ReleaseDC(NULL, screen);
        }
    }
}

EXPORT void RestoreGamma() {
    HDC screen = GetDC(NULL);
    if (!screen) return;
    EnterCriticalSection(&g_stateLock);
    if (g_gammaBackupTaken) SetDeviceGammaRamp(screen, g_originalGammaRamp);
    LeaveCriticalSection(&g_stateLock);
    ReleaseDC(NULL, screen);
}

EXPORT void TestMessageBox() {
    if (active()) MessageBoxA(NULL, "Unknown.dll is working", "Minecraft horror effects",
        MB_OK | MB_ICONWARNING);
}

EXPORT void WallpaperCorruptor(const char* path) {
    if (!active() || !path || !path[0]) return;
    char original[MAX_PATH];
    original[0] = 0;
    SystemParametersInfoA(SPI_GETDESKWALLPAPER, MAX_PATH, original, 0);
    SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0, (void*)path,
        SPIF_UPDATEINIFILE | SPIF_SENDCHANGE);
    sleepWithMessages(10000);
    if (original[0]) {
        SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0, original,
            SPIF_UPDATEINIFILE | SPIF_SENDCHANGE);
    }
}

static LRESULT CALLBACK bsodProc(HWND window, UINT message,
    WPARAM wParam, LPARAM lParam) {
    if (message == WM_PAINT) {
        PAINTSTRUCT paint;
        HDC dc = BeginPaint(window, &paint);
        RECT rect;
        GetClientRect(window, &rect);
        HBRUSH brush = CreateSolidBrush(RGB(0, 0, 170));
        FillRect(dc, &rect, brush);
        DeleteObject(brush);
        SetTextColor(dc, RGB(255, 255, 255));
        SetBkMode(dc, TRANSPARENT);
        TextOutA(dc, 40, 40, "A problem has been detected.", 29);
        TextOutA(dc, 40, 80, "CRITICAL_PROCESS_DIED", 21);
        TextOutA(dc, 40, 120, "This is a simulated horror effect.", 34);
        EndPaint(window, &paint);
        return 0;
    }
    return DefWindowProc(window, message, wParam, lParam);
}

EXPORT void FakeBSODOverlay(int duration) {
    if (!active()) return;
    duration = clampInt(duration, 1, 30000);
    HWND window = createOverlay("UnknownBSODOverlay", bsodProc,
        RGB(0, 0, 170), 255);
    if (!window) return;
    sleepWithMessages((DWORD)duration);
    DestroyWindow(window);
    UnregisterClassA("UnknownBSODOverlay", GetModuleHandle(NULL));
}

EXPORT void WindowGhostIcon(const char* windowTitle, int iconType) {
    if (!active()) return;
    HWND window = findWindow(windowTitle);
    if (!window) return;
    HICON icon = LoadIcon(NULL, iconType == 0 ? IDI_APPLICATION :
        (iconType == 1 ? IDI_ERROR : IDI_WARNING));
    if (!icon) return;
    SendMessage(window, WM_SETICON, ICON_BIG, (LPARAM)icon);
    SendMessage(window, WM_SETICON, ICON_SMALL, (LPARAM)icon);
    InvalidateRect(window, NULL, TRUE);
}

EXPORT void WindowTransparencyGhosting(HWND window, int durationMs) {
    if (!active() || !window) return;
    durationMs = clampInt(durationMs, 1, 30000);
    LONG oldStyle = GetWindowLong(window, GWL_EXSTYLE);
    SetWindowLong(window, GWL_EXSTYLE, oldStyle | WS_EX_LAYERED);
    SetLayeredWindowAttributes(window, 0, 128, LWA_ALPHA);
    sleepWithMessages((DWORD)durationMs);
    SetWindowLong(window, GWL_EXSTYLE, oldStyle);
    SetLayeredWindowAttributes(window, 0, 255, LWA_ALPHA);
    RedrawWindow(window, NULL, NULL, RDW_INVALIDATE | RDW_UPDATENOW);
}

EXPORT void TriggerResolutionSnap(int durationMs) {
    if (!active()) return;
    durationMs = clampInt(durationMs, 1, 30000);
    DEVMODE original;
    ZeroMemory(&original, sizeof(original));
    original.dmSize = sizeof(original);
    if (!EnumDisplaySettings(NULL, ENUM_CURRENT_SETTINGS, &original)) return;
    DEVMODE smaller = original;
    smaller.dmPelsWidth = 640;
    smaller.dmPelsHeight = 480;
    smaller.dmFields = DM_PELSWIDTH | DM_PELSHEIGHT;
    if (ChangeDisplaySettings(&smaller, CDS_FULLSCREEN) != DISP_CHANGE_SUCCESSFUL) return;
    sleepWithMessages((DWORD)durationMs);
    ChangeDisplaySettings(&original, CDS_FULLSCREEN);
}

EXPORT void PulseSystemVolume(int durationMs) {
    if (!active()) return;
    durationMs = clampInt(durationMs, 1, 30000);
    DWORD oldVolume = 0;
    if (waveOutGetVolume((HWAVEOUT)WAVE_MAPPER, &oldVolume) != MMSYSERR_NOERROR) return;
    waveOutSetVolume((HWAVEOUT)WAVE_MAPPER, 0xFFFFFFFF);
    sleepWithMessages((DWORD)durationMs);
    waveOutSetVolume((HWAVEOUT)WAVE_MAPPER, oldVolume);
}

BOOL WINAPI DllMain(HINSTANCE instance, DWORD reason, LPVOID reserved) {
    (void)instance;
    (void)reserved;
    if (reason == DLL_PROCESS_ATTACH) {
        InitializeCriticalSection(&g_stateLock);
        g_lockReady = true;
        srand((unsigned int)time(NULL));
    } else if (reason == DLL_PROCESS_DETACH && g_lockReady) {
        RestoreGamma();
        DeleteCriticalSection(&g_stateLock);
        g_lockReady = false;
    }
    return TRUE;
}
