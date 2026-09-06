# Команды чата (Chat Commands) — мистическая справка

> **Предупреждение:** Некоторые команды не предназначены для обычного игрока. Если вы не уверены в том, что делаете, не вводите их.

---

## /safe

**Назначение:** Безопасный режим.

**Что происходит:**
- Останавливает все текущие мистические эффекты (`GlitchManager.stopAll()`).
- Отключает `Unknown.dll` и восстанавливает гамму монитора.
- Сбрасывает `HorrorState.safeMode = true`.
- Выводит сообщение: `Safe mode enabled.` + `Good luck!`
- Закрывает окно чата.

**Когда использовать:** Если эффекты становятся слишком интенсивными или вы чувствуете, что игра начинает влиять на систему.

---

## /mstinfo

**Назначение:** Информация о состоянии мистика.

**Что происходит:**
- Выводит текущий этап (`Stage`) — от 0 (слабый) до 4 (экстремальный) и 5 (финальный).
- Показывает количество уже сработавших эффектов (`effectsTriggeredCount`).
- Указывает, активен ли туннель (`tunnelSpawned`).
- Показывает текущий `horrorSpeedMultiplier` (например, `x100`).

**Пример вывода:**
```
Stage: Stage 4 (MAX)
Effects: 32
Tunnel: No
Multiplier: x100
```

---

## /event <number>

**Назначение:** Ручной запуск конкретного эффекта (0–50).

**Что происходит:**
- Вызывается `HorrorEffectsManager.triggerSpecificEffect(eventId)`.
- Эффект срабатывает мгновенно, независимо от текущей стадии.
- Чат закрывается после выполнения.

**Доступные диапазоны:** `0` — `50`.

---

## /x — Множитель времени (скрытая команда)

**Назначение:** Ускорение или замедление мистического цикла.

**Что происходит:**
- При `/x100` интервал между эффектами сокращается с 5.5 минут до ~3.3 секунд.
- Эффекты повторяются чаще, стадия 4 достигается быстрее.
- Туннель (`WorldGenBedrockTunnel`) появляется после 30 эффектов с `tunnelSpawned = true`.

---

## Реальная таблица /event ID → эффект

| ID | Класс | Метод | Длительность |
|----|-------|-------|--------------|
| 0 | `HorrorEffects` | `triggerFootstepEcho` | — |
| 1 | `HorrorEffects` | `triggerInventoryGlitchMinor` | — |
| 2 | `HorrorEffects` | `triggerInventoryGlitchMajor` | — |
| 3 | `HorrorEffects` | `playAmbientSound` | — |
| 4 | `HorrorEffects` | `playLowHum` | — |
| 5 | `HorrorEffects` | `increaseFogSlightly` | — |
| 6 | `HorrorEffects` | `triggerModerateFog` | — |
| 7 | `HorrorEffects` | `triggerHeavyFog` | — |
| 8 | `HorrorEffects` | **`triggerBloodTime`** | **15 сек** |
| 9 | `HorrorEffects` | `checkBehindYouGlitch` | — |
| 10 | `UnknownEffects` | `hardwareBeep(37, 500)` | 0.5 сек |
| 11 | `UnknownEffects` | `hardwareBeep(4000, 500)` | 0.5 сек |
| 12 | `UnknownEffects` | `jitterWindow(10, 2000)` | 2 сек |
| 13 | `UnknownEffects` | `jitterWindow(20, 3000)` | 3 сек |
| 14 | `UnknownEffects` | `possessCursor(400, 300, 30, 3000)` | 3 сек |
| 15 | `UnknownEffects` | `possessCursor(200, 200, 60, 4000)` | 4 сек |
| 16 | `UnknownEffects` | `corruptGamma(0)` — Red | 2 сек |
| 17 | `UnknownEffects` | `corruptGamma(1)` — B/W | 2 сек |
| 18 | `UnknownEffects` | **`corruptGamma(2)` — Dark Red** | **2 сек** |
| 19 | `UnknownEffects` | `screenMelt(1500, 8)` | 1.5 сек |
| 20 | `UnknownEffects` | `ghostOverlay(2000)` | 2 сек |
| 21 | `UnknownEffects` | `aggressiveTaskbar(5000)` | 5 сек |
| 22 | `UnknownEffects` | `whisperClipboard(координаты)` | — |
| 23 | `UnknownEffects` | `testMessageBox()` | — |
| 24 | `UnknownEffects` | **`windowTransparency(3000)`** | **3 сек** |
| 25 | `HorrorEffects` | **`triggerInvertedCamera()`** | **4 сек** |
| 26 | `HorrorEffectsManager` | **`triggerFakeTaskkillAlert()`** | **5 сек** |
| 27 | `UnknownEffects` | **`pulseSystemVolume(6000)`** | **6 сек** |
| 28 | `UnknownEffects` | **`resolutionSnap(3000)`** | **3 сек** |
| 29 | `UnknownEffects` | **`ghostIcon(1)`** | **2 сек** |
| 30 | `HorrorEffects` | `spawnPhantomObserver` | — |
| 31 | `HorrorEffects` | `spawnMirrorDouble` | — |
| 35 | `HorrorEffects` | `triggerPartisanInterference` | — |
| 36 | `HorrorEffects` | `generateDisappearingAnomaly` | — |
| 37 | `HorrorEffects` | `triggerGlitchedWindowTitle` | — |
| 38 | `HorrorEffects` | `triggerFakeFrameFreeze` | — |
| 39 | `HorrorEffects` | `triggerViolentShake` | — |
| 40 | `GlitchManager` | `triggerImmediateGlitches(10.0F)` | — |
| 41 | `HorrorEffectsManager` | **`triggerMicrophoneFeedbackScreamer()`** | **2.5 сек** |
| 42 | `ScreenStrobeEffect` | **`trigger()`** | **2 сек** |
| 43 | `FakeFreezeEffect` | **`trigger()`** | **4 сек** |
| 44 | `TemporalVoidDrop` | **`trigger()`** | **2.5 сек** |
| 45 | Комбо | Light: Echo + Glitch + Beep | — |
| 46 | Комбо | Medium: Fog + Behind + Jitter | — |
| 47 | Комбо | Heavy: Blood + Heavy Fog + Red | — |
| 48 | Комбо | Extreme: Melt + Ghost + Shake | — |
| 49 | Комбо | Entity: Phantom + Mirror | — |
| 50 | `HorrorEffects` | **`spawnBedrockTunnel`** | Финал |

---

## Новые эффекты (Stage 1–4)

Эти эффекты вызываются **автоматически** через `triggerStageXEffect` (mod 11, 11, 17, 16) **И** теперь доступны вручную через `/event`:

| Эффект | Метод в Java | Длительность | Этап | Case в Stage | `/event` ID |
|--------|--------------|--------------|------|--------------|-------------|
| WindowTransparencyGhosting | `UnknownEffects.windowTransparency(3000)` | 3 сек | Stage 1 | case 8 | `/event 24` |
| KeyboardInjectedTyping | `triggerKeyboardInjection()` | 3 сек | Stage 1 | case 9 | — |
| FakeTaskkillAlert | `triggerFakeTaskkillAlert()` | 5 сек | Stage 1 | case 10 | `/event 26` |
| InvertedCameraInversion | `HorrorEffects.triggerInvertedCamera()` | 4 сек | Stage 2 | case 8 | `/event 25` |
| SystemVolumeSpikeHeartbeat | `UnknownEffects.pulseSystemVolume(6000)` | 6 сек | Stage 2 | case 9 | `/event 27` |
| WindowGhostIcon | `UnknownEffects.ghostIcon(1)` | 2 сек | Stage 2 | case 10 | `/event 29` |
| MicrophoneFeedbackScreamer | `triggerMicrophoneFeedbackScreamer()` | 2.5 сек | Stage 3 | case 14 | `/event 41` |
| ScreenStrobeDeconstruction | `ScreenStrobeEffect.trigger()` | 2 сек | Stage 3 | case 15 | `/event 42` |
| FakeHardwareFreezeAudioLoop | `FakeFreezeEffect.trigger()` | 4 сек | Stage 3 | case 16 | `/event 43` |
| DisplayResolutionSnap | `UnknownEffects.resolutionSnap(3000)` | 3 сек | Stage 4 | case 14 | `/event 28` |
| EntityTeleportJumpscareVoid | `TemporalVoidDrop.trigger()` | 2.5 сек | Stage 4 | case 15 | `/event 44` |

---

## Скрытые команды

### /powershell

**Что происходит:**
- Добавляется в историю чата, но не обрабатывается как команда.
- Может использоваться для внешних скриптов через `GuiChat`.

---

## Как это связано с мистикой

Каждая команда — это не просто инструмент управления. Они являются **точками входа** в систему `UnknownEffects` и `HorrorEffectsManager`.

- `/safe` — это не просто "отключить игру". Это вызов `UnknownEffects.restoreGamma()` и `UnknownEffects.setEnabled(false)`.
- `/mstinfo` — это чтение внутреннего состояния `HorrorState`, которое обновляется при каждом тике.
- `/event` — это прямой триггер, который обходит естественный цикл `UPDATE_INTERVAL = 1000L`.

**Совет:** если вы видите эффект, который не принадлежит обычному игровому циклу (например, полупрозрачное окно или перевёрнутый экран), это означает, что система уже перешла в активную фазу.

---

## Примечание

Команды работают через `GuiChat.keyTyped()`. Они не используют стандартный механизм `PlayerChatMessageEvent`, а напрямую взаимодействуют с `Minecraft.theMinecraft` и `Unknown.dll` через JNA.

Если вы видите строку:
> `i can hear you typing, [Username]`

Это следствие `triggerKeyboardInjection()`, которая открывает чат и посимвольно вводит текст с именем пользователя Windows.

**Не отправляйте это сообщение.** Оно автоматически закрывается.
