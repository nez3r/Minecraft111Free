#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// Экспорт функций для JNA
#define EXPORT extern "C" __declspec(dllexport)

// Глобальные переменные для отслеживания состояния
static HWND g_overlayWindow = NULL;
static bool g_effectsActive = true;
static WORD g_originalGammaRamp[256 * 3];
static bool g_gammaBackupTaken = false;

// =============================================================================
// 1. GDI_ScreenMelt - Таяние и инверсия экрана
// =============================================================================
EXPORT void GDI_ScreenMelt(int duration, int intensity) {
    if (!g_effectsActive) return;

    HDC hdc = GetDC(NULL);
    if (!hdc) return;

    int screenWidth = GetSystemMetrics(SM_CXSCREEN);
    int screenHeight = GetSystemMetrics(SM_CYSCREEN);

    DWORD startTime = GetTickCount();
    DWORD endTime = startTime + duration;

    while (GetTickCount() < endTime) {
        // Эффект "стекания" экрана
        for (int i = 0; i < intensity; i++) {
            int x = rand() % screenWidth;
            int y = rand() % (screenHeight - 50);
            int width = 50 + rand() % 100;
            int offset = 10 + rand() % 30;

            BitBlt(hdc, x, y + offset, width, screenHeight - y - offset,
                   hdc, x, y, SRCCOPY);
        }

        // Случайная инверсия части экрана
        if (rand() % 3 == 0) {
            RECT rect = {
                rand() % screenWidth,
                rand() % screenHeight,
                rand() % screenWidth,
                rand() % screenHeight
            };
            InvertRect(hdc, &rect);
        }

        Sleep(30);
    }

    ReleaseDC(NULL, hdc);
}

// =============================================================================
// 2. GammaRampCorruptor - Аппаратная порча гаммы монитора
// =============================================================================
EXPORT void GammaRampCorruptor(int mode, int duration) {
    if (!g_effectsActive) return;

    HDC hdc = GetDC(NULL);
    if (!hdc) return;

    // Сохранить оригинальную гамму при первом вызове
    if (!g_gammaBackupTaken) {
        GetDeviceGammaRamp(hdc, g_originalGammaRamp);
        g_gammaBackupTaken = true;
    }

    WORD gammaRamp[256 * 3];

    // Режимы:
    // 0 - Красный монохром
    // 1 - Черно-белый высокий контраст
    // 2 - Темный красный

    for (int i = 0; i < 256; i++) {
        switch(mode) {
            case 0: // Красный монохром
                gammaRamp[i] = (WORD)(i * 256);           // R - полный
                gammaRamp[i + 256] = (WORD)(i * 50);      // G - слабый
                gammaRamp[i + 512] = (WORD)(i * 50);      // B - слабый
                break;
            case 1: // Ч/Б высокий контраст
                {
                    WORD val = (i < 128) ? 0 : 65535;
                    gammaRamp[i] = val;
                    gammaRamp[i + 256] = val;
                    gammaRamp[i + 512] = val;
                }
                break;
            case 2: // Темный красный
                gammaRamp[i] = (WORD)(i * 200);
                gammaRamp[i + 256] = (WORD)(i * 20);
                gammaRamp[i + 512] = (WORD)(i * 20);
                break;
        }
    }

    SetDeviceGammaRamp(hdc, gammaRamp);

    Sleep(duration);

    // Восстановить оригинальную гамму
    SetDeviceGammaRamp(hdc, g_originalGammaRamp);

    ReleaseDC(NULL, hdc);
}

// =============================================================================
// 3. CursorPossession - Сопротивление и перехват курсора
// =============================================================================
EXPORT void CursorPossession(int targetX, int targetY, int strength, int duration) {
    if (!g_effectsActive) return;

    DWORD startTime = GetTickCount();
    DWORD endTime = startTime + duration;

    while (GetTickCount() < endTime) {
        POINT cursor;
        GetCursorPos(&cursor);

        // Плавное притяжение курсора к целевой точке
        int dx = targetX - cursor.x;
        int dy = targetY - cursor.y;

        int moveX = cursor.x + (dx * strength / 100);
        int moveY = cursor.y + (dy * strength / 100);

        // Добавить дрожание
        moveX += (rand() % 5) - 2;
        moveY += (rand() % 5) - 2;

        SetCursorPos(moveX, moveY);

        Sleep(10);
    }
}

// =============================================================================
// 4. WindowPhysicalJitter - Физическое сотрясение окна
// =============================================================================
EXPORT void WindowPhysicalJitter(const char* windowTitle, int intensity, int duration) {
    if (!g_effectsActive) return;

    HWND hwnd = FindWindowA(NULL, windowTitle);
    if (!hwnd) return;

    RECT originalRect;
    GetWindowRect(hwnd, &originalRect);

    DWORD startTime = GetTickCount();
    DWORD endTime = startTime + duration;

    while (GetTickCount() < endTime) {
        int offsetX = (rand() % (intensity * 2)) - intensity;
        int offsetY = (rand() % (intensity * 2)) - intensity;

        SetWindowPos(hwnd, NULL,
                     originalRect.left + offsetX,
                     originalRect.top + offsetY,
                     0, 0,
                     SWP_NOSIZE | SWP_NOZORDER);

        Sleep(50);
    }

    // Вернуть окно на место
    SetWindowPos(hwnd, NULL,
                 originalRect.left,
                 originalRect.top,
                 0, 0,
                 SWP_NOSIZE | SWP_NOZORDER);
}

// =============================================================================
// 5. SystemBeepHardware - Низкоуровневые системные писки
// =============================================================================
EXPORT void SystemBeepHardware(int frequency, int duration) {
    if (!g_effectsActive) return;

    // Низкочастотный тревожный звук
    Beep(frequency, duration);
}

// =============================================================================
// 6. ClipboardWhisper - Подмена буфера обмена
// =============================================================================
EXPORT void ClipboardWhisper(const char* text) {
    if (!g_effectsActive) return;

    if (!OpenClipboard(NULL)) return;

    EmptyClipboard();

    size_t len = strlen(text) + 1;
    HGLOBAL hMem = GlobalAlloc(GMEM_MOVEABLE, len);
    if (hMem) {
        memcpy(GlobalLock(hMem), text, len);
        GlobalUnlock(hMem);
        SetClipboardData(CF_TEXT, hMem);
    }

    CloseClipboard();
}

// =============================================================================
// 7. DesktopOverlayGhost - Прозрачный оверлей на рабочем столе
// =============================================================================
LRESULT CALLBACK OverlayWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_PAINT: {
            PAINTSTRUCT ps;
            HDC hdc = BeginPaint(hwnd, &ps);

            // Нарисовать призрачный силуэт
            HBRUSH brush = CreateSolidBrush(RGB(255, 0, 0));
            SelectObject(hdc, brush);

            // Случайные "трещины" на экране
            for (int i = 0; i < 10; i++) {
                int x1 = rand() % GetSystemMetrics(SM_CXSCREEN);
                int y1 = rand() % GetSystemMetrics(SM_CYSCREEN);
                int x2 = x1 + (rand() % 200) - 100;
                int y2 = y1 + (rand() % 200) - 100;

                MoveToEx(hdc, x1, y1, NULL);
                LineTo(hdc, x2, y2);
            }

            DeleteObject(brush);
            EndPaint(hwnd, &ps);
            break;
        }
        case WM_DESTROY:
            PostQuitMessage(0);
            break;
        default:
            return DefWindowProc(hwnd, msg, wParam, lParam);
    }
    return 0;
}

EXPORT void DesktopOverlayGhost(int duration) {
    if (!g_effectsActive) return;

    WNDCLASSEXA wc = {0};
    wc.cbSize = sizeof(WNDCLASSEXA);
    wc.lpfnWndProc = OverlayWndProc;
    wc.hInstance = GetModuleHandle(NULL);
    wc.lpszClassName = "GhostOverlay";

    RegisterClassExA(&wc);

    int screenWidth = GetSystemMetrics(SM_CXSCREEN);
    int screenHeight = GetSystemMetrics(SM_CYSCREEN);

    HWND hwnd = CreateWindowExA(
        WS_EX_LAYERED | WS_EX_TRANSPARENT | WS_EX_TOPMOST | WS_EX_NOACTIVATE,
        "GhostOverlay",
        "",
        WS_POPUP,
        0, 0, screenWidth, screenHeight,
        NULL, NULL, GetModuleHandle(NULL), NULL
    );

    SetLayeredWindowAttributes(hwnd, 0, 128, LWA_ALPHA); // 50% прозрачность
    ShowWindow(hwnd, SW_SHOW);
    UpdateWindow(hwnd);

    Sleep(duration);

    DestroyWindow(hwnd);
    UnregisterClassA("GhostOverlay", GetModuleHandle(NULL));
}

// =============================================================================
// 8. TaskbarAggression - Агрессивное поведение панели задач
// =============================================================================
EXPORT void TaskbarAggression(const char* windowTitle, int duration) {
    if (!g_effectsActive) return;

    HWND hwnd = FindWindowA(NULL, windowTitle);
    if (!hwnd) return;

    FLASHWINFO fwi;
    fwi.cbSize = sizeof(FLASHWINFO);
    fwi.hwnd = hwnd;
    fwi.dwFlags = FLASHW_ALL | FLASHW_TIMERNOFG;
    fwi.uCount = duration / 500; // Мигать каждые 500мс
    fwi.dwTimeout = 500;

    FlashWindowEx(&fwi);

    // Системные звуки ошибки
    for (int i = 0; i < 3; i++) {
        MessageBeep(MB_ICONERROR);
        Sleep(duration / 4);
    }
}

// =============================================================================
// Утилиты для управления
// =============================================================================
EXPORT void SetEffectsEnabled(bool enabled) {
    g_effectsActive = enabled;
}

EXPORT void RestoreGamma() {
    if (g_gammaBackupTaken) {
        HDC hdc = GetDC(NULL);
        if (hdc) {
            SetDeviceGammaRamp(hdc, g_originalGammaRamp);
            ReleaseDC(NULL, hdc);
        }
    }
}

// =============================================================================
// 9. TestMessageBox - Тестовый MessageBox для проверки DLL
// =============================================================================
EXPORT void TestMessageBox() {
    MessageBoxA(NULL, "unknown", "", MB_OK | MB_ICONWARNING | MB_SYSTEMMODAL);
}

// =============================================================================
// DLL Entry Point
// =============================================================================
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved) {
    switch (fdwReason) {
        case DLL_PROCESS_ATTACH:
            srand((unsigned int)time(NULL));
            break;
        case DLL_PROCESS_DETACH:
            RestoreGamma();
            if (g_overlayWindow) {
                DestroyWindow(g_overlayWindow);
            }
            break;
    }
    return TRUE;
}
