package net.minecraft.src;

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.io.File;

/**
 * Native Windows effects через Unknown.dll
 * Использует JNA для вызова нативных функций
 */
public class UnknownEffects {

    // JNA интерфейс для загрузки Unknown.dll
    public interface UnknownDLL extends Library {
        UnknownDLL INSTANCE = loadUnknownDLL();

        // 1. GDI Screen Melt - Таяние экрана
        void GDI_ScreenMelt(int duration, int intensity);

        // 2. Gamma Ramp Corruptor - Порча гаммы монитора
        void GammaRampCorruptor(int mode, int duration);

        // 3. Cursor Possession - Перехват курсора
        void CursorPossession(int targetX, int targetY, int strength, int duration);

        // 4. Window Physical Jitter - Тряска окна
        void WindowPhysicalJitter(String windowTitle, int intensity, int duration);

        // 5. System Beep Hardware - Системные писки
        void SystemBeepHardware(int frequency, int duration);

        // 6. Clipboard Whisper - Подмена буфера обмена
        void ClipboardWhisper(String text);

        // 7. Desktop Overlay Ghost - Оверлей призрака
        void DesktopOverlayGhost(int duration);

        // 8. Taskbar Aggression - Агрессивная панель задач
        void TaskbarAggression(String windowTitle, int duration);

        // 9. Test MessageBox - Тестовый MessageBox
        void TestMessageBox();

        // Утилиты
        void SetEffectsEnabled(boolean enabled);
        void RestoreGamma();
    }

    private static boolean dllLoaded = false;
    private static boolean dllAvailable = false;

    /**
     * Загрузка Unknown.dll с правильным путём
     */
    private static UnknownDLL loadUnknownDLL() {
        try {
            // Попытка 1: Абсолютный путь к libraries/natives/Unknown.dll
            File dllFile = new File("libraries/natives/Unknown.dll").getAbsoluteFile();
            if (dllFile.exists()) {
                System.out.println("[Unknown.dll] Loading from: " + dllFile.getAbsolutePath());
                return (UnknownDLL) Native.loadLibrary(dllFile.getAbsolutePath(), UnknownDLL.class);
            }

            // Попытка 2: Относительный путь с обратными слешами
            dllFile = new File("libraries\\natives\\Unknown.dll").getAbsoluteFile();
            if (dllFile.exists()) {
                System.out.println("[Unknown.dll] Loading from: " + dllFile.getAbsolutePath());
                return (UnknownDLL) Native.loadLibrary(dllFile.getAbsolutePath(), UnknownDLL.class);
            }

            // Попытка 3: Текущая директория
            dllFile = new File("Unknown.dll").getAbsoluteFile();
            if (dllFile.exists()) {
                System.out.println("[Unknown.dll] Loading from: " + dllFile.getAbsolutePath());
                return (UnknownDLL) Native.loadLibrary(dllFile.getAbsolutePath(), UnknownDLL.class);
            }

            System.err.println("[Unknown.dll] Not found in any expected location");
            System.err.println("[Unknown.dll] Tried: " + new File("libraries/natives/Unknown.dll").getAbsolutePath());
            return null;
        } catch (Exception e) {
            System.err.println("[Unknown.dll] Failed to load: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Инициализация Unknown.dll
     */
    public static void init() {
        if (dllLoaded) return;

        try {
            // Проверить, что INSTANCE загружен
            if (UnknownDLL.INSTANCE != null) {
                UnknownDLL.INSTANCE.SetEffectsEnabled(true);
                dllAvailable = true;
                dllLoaded = true;
                System.out.println("[Unknown.dll] Loaded successfully");
            } else {
                dllAvailable = false;
                dllLoaded = true;
                System.err.println("[Unknown.dll] Failed to load: INSTANCE is null");
            }
        } catch (Exception e) {
            dllAvailable = false;
            dllLoaded = true;
            System.err.println("[Unknown.dll] Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Проверка доступности DLL
     */
    public static boolean isAvailable() {
        if (!dllLoaded) init();
        return dllAvailable;
    }

    // =============================================================================
    // Обёртки для безопасного вызова функций
    // =============================================================================

    /**
     * 1. Эффект таяния экрана
     * @param duration Длительность в миллисекундах
     * @param intensity Интенсивность (1-10)
     */
    public static void screenMelt(int duration, int intensity) {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.GDI_ScreenMelt(duration, intensity)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. Порча гаммы монитора
     * @param mode 0=Красный монохром, 1=Ч/Б контраст, 2=Темный красный
     * @param duration Длительность в миллисекундах
     */
    public static void corruptGamma(int mode, int duration) {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.GammaRampCorruptor(mode, duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 3. Перехват курсора (притягивает к точке)
     * @param targetX Целевая X координата
     * @param targetY Целевая Y координата
     * @param strength Сила притяжения (1-100)
     * @param duration Длительность в миллисекундах
     */
    public static void possessCursor(int targetX, int targetY, int strength, int duration) {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.CursorPossession(targetX, targetY, strength, duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 4. Физическая тряска окна
     * @param intensity Интенсивность (5-20 пикселей)
     * @param duration Длительность в миллисекундах
     */
    public static void jitterWindow(int intensity, int duration) {
        if (!isAvailable()) return;
        try {
            String title = "Minecraft 1.1.1 Free";
            new Thread(() -> UnknownDLL.INSTANCE.WindowPhysicalJitter(title, intensity, duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 5. Системный писк (низкоуровневый)
     * @param frequency Частота (37-4000 Гц)
     * @param duration Длительность в миллисекундах
     */
    public static void hardwareBeep(int frequency, int duration) {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.SystemBeepHardware(frequency, duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 6. Подмена буфера обмена
     * @param text Текст для вставки
     */
    public static void whisperClipboard(String text) {
        if (!isAvailable()) return;
        try {
            UnknownDLL.INSTANCE.ClipboardWhisper(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 7. Призрачный оверлей на экране
     * @param duration Длительность в миллисекундах
     */
    public static void ghostOverlay(int duration) {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.DesktopOverlayGhost(duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 8. Агрессивное мигание панели задач
     * @param duration Длительность в миллисекундах
     */
    public static void aggressiveTaskbar(int duration) {
        if (!isAvailable()) return;
        try {
            String title = "Minecraft 1.1.1 Free";
            new Thread(() -> UnknownDLL.INSTANCE.TaskbarAggression(title, duration)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Включить/выключить эффекты
     */
    public static void setEnabled(boolean enabled) {
        if (!isAvailable()) return;
        try {
            UnknownDLL.INSTANCE.SetEffectsEnabled(enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Восстановить гамму монитора
     */
    public static void restoreGamma() {
        if (!isAvailable()) return;
        try {
            UnknownDLL.INSTANCE.RestoreGamma();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 9. Тестовый MessageBox для проверки Unknown.dll
     */
    public static void testMessageBox() {
        if (!isAvailable()) return;
        try {
            new Thread(() -> UnknownDLL.INSTANCE.TestMessageBox()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
