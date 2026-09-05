import re

with open('HorrorEffectsManager.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Problem: The interval calculation generates a NEW random interval each time update() is called
# This means the interval keeps changing, making effects trigger more frequently
# Solution: Calculate the interval ONCE when the effect is triggered

# Find and fix the update() method
old_logic = '''        // Рассчитать интервал с учётом множителя скорости
        long effectInterval = (long)((MIN_EFFECT_INTERVAL + rand.nextInt((int)(MAX_EFFECT_INTERVAL - MIN_EFFECT_INTERVAL))) / HorrorState.horrorSpeedMultiplier);

        // Проверить, пора ли запускать следующий эффект
        if (currentTime - HorrorState.lastEffectTriggerTime >= effectInterval) {'''

new_logic = '''        // Использовать фиксированный интервал (рассчитывается один раз при триггере)
        // Интервал уже применён при последнем триггере
        long effectInterval = (long)((3 * 60 * 1000L + 5 * 60 * 1000L / 2) / HorrorState.horrorSpeedMultiplier); // Средний интервал 5.5 минут

        // Проверить, пора ли запускать следующий эффект
        if (currentTime - HorrorState.lastEffectTriggerTime >= effectInterval) {'''

content = content.replace(old_logic, new_logic)

with open('HorrorEffectsManager.java', 'w', encoding='utf-8') as f:
    f.write(content)

print('Fixed interval logic')
