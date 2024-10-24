## Задание 40

Рассмотрим в качестве примера мобильное приложение по доставке банковских продуктов.

### Определение ключевых свойств системы

#### Время отклика приложения:

Узкий диапазон: 100-200 мс

Широкий диапазон: 100-500 мс

Инвариант: STRONG (все состояния должны обеспечивать время отклика в заданном диапазоне).


#### Использование оперативной памяти:

Узкий диапазон: 50-70 МБ

Широкий диапазон: 50-150 МБ

Инвариант: TRIPWIRE (приложение может начать с более высокого потребления памяти, но должно стабилизироваться в узком диапазоне).

#### Доступность сервиса:

Узкий диапазон: 99%-99.9%

Широкий диапазон: 95%-99%

Инвариант: RESILIENT (приложение должно восстанавливаться после сбоев и поддерживать доступность).

#### Потребление заряда батареи:

Узкий диапазон: 5-10% заряда в час

Широкий диапазон: 5-20% заряда в час

Инвариант: TRIPWIRE (приложение может начать с более высокого потребления, но должно стабилизироваться в узком диапазоне)

#### Частота обновлений данных:

Узкий диапазон: каждые 5-10 секунд

Широкий диапазон: каждые 5-30 секунд

Инвариант: WEAK (приложение может обновляться реже, но должно обеспечивать хотя бы одно обновление в заданном времени).

### Укрепление инвариантов
Для усиления инвариантов можно предпринять следующие шаги:

- Оптимизация кода для снижения времени отклика и использования памяти.
- Внедрение кэширования данных, чтобы уменьшить нагрузку на сервер и повысить производительность работы приложения. 
- Отказ от постоянно обновляемых виджетов с целью экономии ресурсов батареи

### Характеристика скорости восстановления
Характеристику скорости восстановления мы можем добавить, например, через реализацию механизма мониторинга состояния приложения и автоматического перезапуска в случае сбоя. 
Это может быть достигнуто с помощью:
- Использования библиотек, таких как WorkManager, для управления фоновыми задачами и гарантии их выполнения. 
- Внедрения логирования и анализа сбоев, что поможет выявить узкие места и улучшить время восстановления.

## Выводы
Занятие позволило взглянуть на понятие о надежности системы под другим углом. Раньше, когда я
говорил о надежности, то имел ввиду прежде всего количество багов, крашей, непредсказуемой работы и тп.
Теперь же у меня появились четкие метрики, по которым также можно и нужно судить о надежности системы.
В контексте формализации свойств надежности системы мне наконец стало ясно, что ине даст Profiler, для
чего использовать данные профилирования. Раньше думал, что это полезно, и что нужно стремиться к снижению
энергопотребления, потребления памяти, нагрузки на сеть, но особо не придавал этому значения, как и
другие разработчики из моей команды. Теперь, смотря на эти метрики, я могу ориентироваться на
оптимальный уровень, и если в какой-то момент какие-то значения выходят за допустимый диапазон, то
надо срочно что-то переделывать, так как мы теряем контроль над системой, что может привести в дальнейшем
к краху проекта.