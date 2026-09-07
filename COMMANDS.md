# Команды чата (Chat Commands) — мистическая справка

> **Предупреждение:** Некоторые команды не предназначены для обычного игрока. Если вы не уверены в том, что делаете, не вводите их.

---

## /safe

**Назначение:** Безопасный режим.

**Что происходит:**
- Останавливает финальную BSOD/native crash-последовательность.
- Восстанавливает гамму, не сбрасывая планировщик.
- Выводит сообщение: `Only the final BSOD was stopped.`
- Закрывает окно чата.

**Когда использовать:** Если эффекты становятся слишком интенсивными или вы чувствуете, что игра начинает влиять на систему.

---

## /mstinfo

**Назначение:** Информация о состоянии мистика.

**Что происходит:**
- Выводит текущий этап: Stage 1 (Weak), Stage 2 (Medium), Stage 3 (Strong), Stage 4 (Extreme) или Tunnel phase.
- Показывает количество уже сработавших эффектов (`effectsTriggeredCount`).
- Указывает, активен ли туннель (`tunnelSpawned`).
- Показывает текущий `horrorSpeedMultiplier` (например, `x100`).

**Пример вывода:**
```
Stage: Stage 4 (Extreme)
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

**Доступные ID:** `0`–`32`, `35`–`58`. ID `33`–`34` зарезервированы. ID `50` запускает финальный бедроковый туннель.

Команда принимает целое число после `/event`, например `/event 8` или `/event 50`.
Ручной запуск не расходует эффект из текущего этапа, не меняет его порядок и не сбрасывает таймер.

---

## /x — Множитель времени (скрытая команда)

**Назначение:** Ускорение или замедление мистического цикла.

**Что происходит:**
- При входе в мир первый интервал равен 5 минутам. После запуска каждого эффекта следующий интервал выбирается один раз случайно из 4–8 минут.
- Повторы одного эффекта (1–3 раза) используют укороченный интервал.
- Множитель применяется к уже выбранному интервалу: `/x100` превращает 4–8 минут примерно в 2.4–4.8 секунды.
- После полного прохождения четырёх этапов запускается финальная последовательность `56 → 57 → 58 → 50`.
- После события 58 (20 секунд) событие 50 запускает бедроковый туннель сразу. Это работает и в автоматическом цикле, и при `/event 58`.
- Ночь можно пропустить обычным сном; хоррор-эффекты больше не блокируют смену времени.

---

## Реальная таблица /event ID → эффект

| ID | Класс | Метод | Длительность |
|----|-------|-------|--------------|
| 0 | `HorrorEffects` | `triggerFootstepEcho` | — |
| 1 | `HorrorEffects` | `triggerInventoryGlitchMinor` | — |
| 2 | `HorrorEffects` | `triggerInventoryGlitchMajor` | — |
| 3 | `DynamicWindowTitle` | **`AMBIENT WHISPER`** | 3 сек |
| 4 | `HorrorEffects` | `playLowHum` — title glitch | 3 сек |
| 5 | `HorrorEffects` | `increaseFogSlightly` | — |
| 6 | `HorrorEffects` | `triggerModerateFog` | — |
| 7 | `HorrorEffects` | `triggerHeavyFog` | — |
| 8 | `BloodTimeCycle` | **red fog and sky tint** | **15 сек** |
| 9 | `HorrorEffects` | `checkBehindYouGlitch` | — |
| 10 | `DynamicWindowTitle` | **`LOW FREQUENCY`** | 1.5 сек |
| 11 | `DynamicWindowTitle` | **`HIGH FREQUENCY`** | 1.5 сек |
| 12 | `UnknownEffects` | `jitterWindow(10, 2000)` | 2 сек |
| 13 | `UnknownEffects` | `jitterWindow(20, 3000)` | 3 сек |
| 14 | `UnknownEffects` | `possessCursor(400, 300, 30, 3000)` | 3 сек |
| 15 | `UnknownEffects` | `possessCursor(200, 200, 60, 4000)` | 4 сек |
| 16 | `UnknownEffects` | `corruptGamma(0)` — Red only | 2 сек |
| 17 | `UnknownEffects` | `corruptGamma(1)` — B/W | 2 сек |
| 18 | `UnknownEffects` | **`corruptGamma(2)` — Dark Red** | **2 сек** |
| 19 | `UnknownEffects` | `screenMelt(1500, 8)` | 1.5 сек |
| 20 | `UnknownEffects` | `ghostOverlay(2000)` | 2 сек |
| 21 | `UnknownEffects` | `aggressiveTaskbar(5000)` | 5 сек |
| 22 | `UnknownEffects` | `whisperClipboard(координаты)` | — |
| 23 | `UnknownEffects` | `testMessageBox()` | — |
| 24 | `UnknownEffects` | **`windowTransparency(10000)`** | **10 сек** |
| 25 | `HorrorEffects` + `DynamicWindowTitle` | **`UPSIDE DOWN`** | **4 сек** |
| 26 | `DynamicWindowTitle` | **`???`** | **5 сек** |
| 27 | `HorrorEffects` | **`triggerHeartbeat()`** | **мгновенно** |
| 28 | `UnknownEffects` | **`resolutionSnap(3000)`** | **3 сек** |
| 29 | `UnknownEffects` | **`ghostIcon(1)`** | **2 сек** |
| 30 | `HorrorEffects` + `DynamicWindowTitle` | `PHANTOM OBSERVER` | 4 сек |
| 31 | `HorrorEffects` | `spawnMirrorDouble` | — |
| 32 | `HorrorEffectsManager` | **`triggerKeyboardInjection()`** | 3 сек |
| 33–34 | — | Зарезервированы, сейчас не реализованы | — |
| 35 | `HorrorEffects` + `DynamicWindowTitle` | `PARTISAN INTERFERENCE` | 4 сек |
| 36 | `HorrorEffects` + `DynamicWindowTitle` | `DISAPPEARING ANOMALY` | 4 сек |
| 37 | `HorrorEffects` | `triggerGlitchedWindowTitle` | — |
| 38 | `HorrorEffects` | `triggerFakeFrameFreeze` | — |
| 39 | `HorrorEffects` | `triggerViolentShake` | — |
| 40 | `GlitchManager` | `triggerImmediateGlitches(10.0F)` | — |
| 41 | `ScreenStrobeEffect` + `DynamicWindowTitle` | **`CAN YOU HEAR ME?`** | **2.5 сек** |
| 42 | `ScreenStrobeEffect` + title | **`LOOK AWAY`** | **2 сек** |
| 43 | `ScreenStrobeEffect` + `DynamicWindowTitle` | **`PROCESSING...`** | **4 сек** |
| 44 | `TemporalVoidDrop` | **safe fall with no fall damage** | **4 сек** |
| 45 | `RenderHorrorEffects` | `TessellatorPolygonWindingGlitch` | 5 сек |
| 46 | `RenderHorrorEffects` | `TextureMatrixTileShift` + `glitch1.ogg` | 5 сек |
| 47 | `RenderHorrorEffects` | `LightmapCorruptStrobe` + `glitch3.ogg` | 3 сек |
| 48 | `RenderHorrorEffects` | `FrustumCullingLying` | 5 сек |
| 49 | `RenderHorrorEffects` | `ColorLogicOpXORFlash` + `glitch5.ogg` | 18 сек |
| 50 | `HorrorEffects` | **`spawnBedrockTunnel`** | Сразу запускает туннель |
| 51 | `RenderHorrorEffects` | `ModelBoneRotationJitter` + `glitch8.ogg` | 4 сек × 3 |
| 52 | `RenderHorrorEffects` | `ViewportStrobeSlicedBands` + `glitch12.ogg` | 2 сек × 2 |
| 53 | `RenderHorrorEffects` | `ClearColorFlashCorrupt` + `glitch4.ogg` | 7 сек |
| 54 | `RenderHorrorEffects` | `ProjectionShearingMatrix` + `glitch9.ogg` | 30 сек |
| 55 | `RenderHorrorEffects` | `GUIBlendAdditiveStrobe` | 5 сек |
| 56 | `RenderHorrorEffects` | `BitwiseMatrixShatter` + `glitch11.ogg` | 22 сек (звук ограничен 22 сек) |
| 57 | `RenderHorrorEffects` | `ZBufferBleedCollapse` + `glitch6.ogg` | 12 сек × 2 |
| 58 | `RenderHorrorEffects` | `TextureAtlasDeconstruction` + `glitch2.ogg` | 20 сек, затем туннель |

---

## Новые эффекты (Stage 1–4)

Автоматический планировщик использует собственные наборы Stage 1–4 (11, 8, 17 и 26 эффектов) в случайном порядке. После завершения Stage 4 запускается финальная последовательность `56 → 57 → 58`, затем автоматически вызывается событие 50 и появляется бедроковый туннель. ID `50` также можно вызвать вручную. `/event` запускает эффект сразу, не изменяя порядок планировщика.

| Эффект | Метод в Java | Длительность | Этап | Case в Stage | `/event` ID |
|--------|--------------|--------------|------|--------------|-------------|
| WindowTransparencyGhosting | `UnknownEffects.windowTransparency(10000)` | 10 сек | Stage 1 | case 8 | `/event 24` |
| KeyboardInjectedTyping | `triggerKeyboardInjection()` | 3 сек | Stage 1 | case 9 | `/event 32` |
| FakeTaskkillAlert | `DynamicWindowTitle.triggerTitle("???")` | 5 сек | Stage 1 | case 10 | `/event 26` |
| InvertedCameraInversion | `HorrorEffects.triggerInvertedCamera()` + `UPSIDE DOWN` | 4 сек | Stage 2 | case 8 | `/event 25` |
| SystemVolumeSpikeHeartbeat | `HorrorEffects.triggerHeartbeat()` | мгновенно | Stage 2 | case 9 | `/event 27` |
| WindowGhostIcon | `UnknownEffects.ghostIcon(1)` | 2 сек | Stage 2 | case 10 | `/event 29` |
| MicrophoneFeedbackScreamer | `ScreenStrobeEffect` + title | 2.5 сек | Stage 3 | case 14 | `/event 41` |
| ScreenStrobeDeconstruction | `ScreenStrobeEffect.trigger()` + title | 2 сек | Stage 3 | case 15 | `/event 42` |
| FakeHardwareFreezeAudioLoop | `ScreenStrobeEffect` + title | 4 сек | Stage 3 | case 16 | `/event 43` |
| DisplayResolutionSnap | `UnknownEffects.resolutionSnap(3000)` | 3 сек | Stage 4 | case 14 | `/event 28` |
| EntityTeleportJumpscareVoid | `TemporalVoidDrop.trigger()` — safe fall | 4 сек | Stage 4 | case 15 | `/event 44` |
| TessellatorPolygonWindingGlitch | `RenderHorrorEffects.trigger(45, 5000)` | 5 сек | Stage 4 | case 16 | `/event 45` |
| TextureMatrixTileShift | `RenderHorrorEffects.trigger(46, 5000)` | 5 сек | Stage 4 | case 17 | `/event 46` |
| LightmapCorruptStrobe | `RenderHorrorEffects.trigger(47, 3000)` | 3 сек | Stage 4 | case 18 | `/event 47` |
| FrustumCullingLying | `RenderHorrorEffects.trigger(48, 5000)` | 5 сек | Stage 4 | case 19 | `/event 48` |
| ColorLogicOpXORFlash | `RenderHorrorEffects.trigger(49, 18000)` | 18 сек | Stage 4 | case 20 | `/event 49` |
| ModelBoneRotationJitter | `RenderHorrorEffects.trigger(51, 4000, 3)` | 4 сек × 3 | Stage 4 | case 21 | `/event 51` |
| ViewportStrobeSlicedBands | `RenderHorrorEffects.trigger(52, 2000, 2)` | 2 сек × 2 | Stage 4 | case 22 | `/event 52` |
| ClearColorFlashCorrupt | `RenderHorrorEffects.trigger(53, 7000)` | 7 сек | Stage 4 | case 23 | `/event 53` |
| ProjectionShearingMatrix | `RenderHorrorEffects.trigger(54, 30000)` | 30 сек | Stage 4 | case 24 | `/event 54` |
| GUIBlendAdditiveStrobe | `RenderHorrorEffects.trigger(55, 5000)` | 5 сек | Stage 4 | case 25 | `/event 55` |
| BitwiseMatrixShatter | `RenderHorrorEffects.trigger(56, 22000)` + `glitch11.ogg` | 22 сек | Final sequence | — | `/event 56` |
| ZBufferBleedCollapse | `RenderHorrorEffects.trigger(57, 12000, 2)` + `glitch6.ogg` | 12 сек × 2 | Final sequence | — | `/event 57` |
| TextureAtlasDeconstruction | `RenderHorrorEffects.trigger(58, 20000)` + `glitch2.ogg` | 20 сек, затем event 50 | Final sequence | — | `/event 58` |

---

## Скрытые команды

### /powershell

**Что происходит:**
- Добавляется в историю чата, но не обрабатывается как команда.
- Может использоваться для внешних скриптов через `GuiChat`.

---

## Как это связано с мистикой

Каждая команда — это не просто инструмент управления. Они являются **точками входа** в систему `UnknownEffects` и `HorrorEffectsManager`.

- `/safe` останавливает только финальную BSOD/native crash-последовательность и восстанавливает гамму.
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
