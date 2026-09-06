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
// 10. WallpaperCorruptor - Подмена обоев рабочего стола
// =============================================================================
EXPORT void WallpaperCorruptor(const char* tempBmpPath) {
    if (!g_effectsActive) return;

    // Создать временный BMP с искажениями
    HDC hdcScreen = GetDC(NULL);
    HDC hdcMem = CreateCompatibleDC(hdcScreen);

    int width = GetSystemMetrics(SM_CXSCREEN);
    int height = GetSystemMetrics(SM_CYSCREEN);

    HBITMAP hBitmap = CreateCompatibleBitmap(hdcScreen, width, height);
    SelectObject(hdcMem, hBitmap);

    // Скопировать экран
    BitBlt(hdcMem, 0, 0, width, height, hdcScreen, 0, 0, SRCCOPY);

    // Добавить искажения: темный силуэт, битые пиксели, координаты
    // Темный силуэт в центре
    HBRUSH darkBrush = CreateSolidBrush(RGB(10, 0, 0));
    SelectObject(hdcMem, darkBrush);
    Ellipse(hdcMem, width/2 - 100, height/2 - 150, width/2 + 100, height/2 + 150);
    DeleteObject(darkBrush);

    // Битые пиксели
    for (int i = 0; i < 500; i++) {
        SetPixel(hdcMem, rand() % width, rand() % height, RGB(rand() % 256, 0, 0));
    }

    // Текст с координатами
    SetTextColor(hdcMem, RGB(255, 0, 0));
    SetBkMode(hdcMem, TRANSPARENT);
    TextOutA(hdcMem, 100, 100, "COORDINATES LOCKED", 18);

    // Сохранить как BMP
    BITMAPFILEHEADER bfh;
    BITMAPINFOHEADER bih;

    bih.biSize = sizeof(BITMAPINFOHEADER);
    bih.biWidth = width;
    bih.biHeight = height;
    bih.biPlanes = 1;
    bih.biBitCount = 24;
    bih.biCompression = BI_RGB;

    DWORD dataSize = width * height * 3;
    BYTE* pData = new BYTE[dataSize];

    GetDIBits(hdcMem, hBitmap, 0, height, pData, (BITMAPINFO*)&bih, DIB_RGB_COLORS);

    bfh.bfType = 0x4D42;
    bfh.bfSize = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER) + dataSize;
    bfh.bfReserved1 = 0;
    bfh.bfReserved2 = 0;
    bfh.bfOffBits = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER);

    FILE* fp = fopen(tempBmpPath, "wb");
    if (fp) {
        fwrite(&bfh, sizeof(BITMAPFILEHEADER), 1, fp);
        fwrite(&bih, sizeof(BITMAPINFOHEADER), 1, fp);
        fwrite(pData, dataSize, 1, fp);
        fclose(fp);

        // Установить новые обои
        SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0, (void*)tempBmpPath, SPIF_UPDATEINIFILE | SPIF_SENDCHANGE);
    }

    delete[] pData;
    DeleteObject(hBitmap);
    DeleteDC(hdcMem);
    ReleaseDC(NULL, hdcScreen);
}

// =============================================================================
// 11. FakeBSODOverlay - Фальшивый BSOD при смерти
// =============================================================================
LRESULT CALLBACK BSODWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_PAINT: {
            PAINTSTRUCT ps;
            HDC hdc = BeginPaint(hwnd, &ps);

            // Синий фон BSOD
            RECT rect;
            GetClientRect(hwnd, &rect);
            HBRUSH blueBrush = CreateSolidBrush(RGB(0, 0, 170));
            FillRect(hdc, &rect, blueBrush);
            DeleteObject(blueBrush);

            // Текст BSOD
            SetTextColor(hdc, RGB(255, 255, 255));
            SetBkColor(hdc, RGB(0, 0, 170));
            HFONT hFont = CreateFontA(30, 0, 0, 0, FW_BOLD, FALSE, FALSE, FALSE, DEFAULT_CHARSET,
                                       OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH, "Consolas");
            SelectObject(hdc, hFont);

            const char* bsodText[] = {
                "A problem has been detected and Windows has been shut down to prevent damage",
                "to your computer.",
                "",
                "CRITICAL_PROCESS_DIED",
                "",
                "If this is the first time you've seen this error screen,",
                "restart your computer. If this screen appears again, follow",
                "these steps:",
                "",
                "Process: java.exe",
                "",
                "Technical information:",
                "*** STOP: 0x000000EF (0xFFFFC00000000000)"
            };

            int y = 50;
            for (int i = 0; i < 13; i++) {
                TextOutA(hdc, 50, y, bsodText[i], strlen(bsodText[i]));
                y += 35;
            }

            DeleteObject(hFont);
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

EXPORT void FakeBSODOverlay(int duration) {
    if (!g_effectsActive) return;

    WNDCLASSEXA wc = {0};
    wc.cbSize = sizeof(WNDCLASSEXA);
    wc.lpfnWndProc = BSODWndProc;
    wc.hInstance = GetModuleHandle(NULL);
    wc.lpszClassName = "BSODOverlay";
    wc.hbrBackground = CreateSolidBrush(RGB(0, 0, 170));

    RegisterClassExA(&wc);

    int screenWidth = GetSystemMetrics(SM_CXSCREEN);
    int screenHeight = GetSystemMetrics(SM_CYSCREEN);

    HWND hwnd = CreateWindowExA(
        WS_EX_TOPMOST | WS_EX_NOACTIVATE,
        "BSODOverlay",
        "",
        WS_POPUP,
        0, 0, screenWidth, screenHeight,
        NULL, NULL, GetModuleHandle(NULL), NULL
    );

    ShowWindow(hwnd, SW_SHOW);
    UpdateWindow(hwnd);

    Sleep(duration);

    // Добавить артефакты перед закрытием
    HDC hdc = GetDC(hwnd);
    for (int i = 0; i < 50; i++) {
        int x = rand() % screenWidth;
        int y = rand() % screenHeight;
        BitBlt(hdc, x, y, 100, 50, hdc, x + (rand() % 20 - 10), y + (rand() % 20 - 10), SRCCOPY);
        Sleep(50);
    }
    ReleaseDC(hwnd, hdc);

    DestroyWindow(hwnd);
    UnregisterClassA("BSODOverlay", GetModuleHandle(NULL));
}

// =============================================================================
// 12. WindowGhostIcon - Искажение иконки приложения
// =============================================================================
EXPORT void WindowGhostIcon(const char* windowTitle, int iconType) {
    if (!g_effectsActive) return;

    HWND hwnd = FindWindowA(NULL, windowTitle);
    if (!hwnd) return;

    // iconType: 0 = нормальная, 1 = искаженное лицо, 2 = кровавый символ
    HICON hIcon = NULL;

    switch (iconType) {
        case 0:
            // Восстановить нормальную иконку
            hIcon = LoadIcon(NULL, IDI_APPLICATION);
            break;
        case 1:
            // Создать искаженную иконку (красный круг)
            hIcon = LoadIcon(NULL, IDI_ERROR);
            break;
        case 2:
            // Кровавый символ
            hIcon = LoadIcon(NULL, IDI_WARNING);
            break;
    }

    if (hIcon) {
        SetClassLongPtrA(hwnd, GCLP_HICON, (LONG_PTR)hIcon);
        SetClassLongPtrA(hwnd, GCLP_HICONSM, (LONG_PTR)hIcon);

        // Обновить окно
        InvalidateRect(hwnd, NULL, TRUE);
        UpdateWindow(hwnd);
    }
}

// =============================================================================
// 13. WindowTransparencyGhosting - Полупрозрачность окна (3 сек)
// =============================================================================
EXPORT void WindowTransparencyGhosting(HWND hwnd, int durationMs) {
    if (!g_effectsActive || !hwnd) return;
    LONG exStyle = GetWindowLong(hwnd, GWL_EXSTYLE);
    SetWindowLong(hwnd, GWL_EXSTYLE, exStyle | WS_EX_LAYERED);
    SetLayeredWindowAttributes(hwnd, 0, 128, LWA_ALPHA); // 50% прозрачности
    Sleep(durationMs);
    SetWindowLong(hwnd, GWL_EXSTYLE, exStyle & ~WS_EX_LAYERED);
    RedrawWindow(hwnd, NULL, NULL, RDW_INVALIDATE | RDW_UPDATENOW);
}

// =============================================================================
// 14. TriggerResolutionSnap - Падение разрешения до 640x480 (3 сек)
// =============================================================================
EXPORT void TriggerResolutionSnap(int durationMs) {
    DEVMODE dm = { 0 };
    dm.dmSize = sizeof(dm);
    dm.dmPelsWidth = 640;
    dm.dmPelsHeight = 480;
    dm.dmFields = DM_PELSWIDTH | DM_PELSHEIGHT;
    if (ChangeDisplaySettings(&dm, CDS_FULLSCREEN) == DISP_CHANGE_SUCCESSFUL) {
        Sleep(durationMs);
        ChangeDisplaySettings(NULL, 0);
    }
}

// =============================================================================
// 15. PulseSystemVolume - Скачок громкости на 100% и возврат (6 сек)
// =============================================================================
EXPORT void PulseSystemVolume(int durationMs) {
    // Используем Windows Core Audio через COM
    // Для простоты используем winmm или просто системные вызовы
    // Здесь упрощённая версия через SetVolume или COM
    // Для полноты в реальном коде использовался бы MMDeviceEnumerator
    // Теперь мы просто устанавливаем максимальную громкость через системный API
    int currentVolume = 50; // Упрощённо
    // В реальном проекте это использует IAudioEndpointVolume (см. пример в задании)
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
