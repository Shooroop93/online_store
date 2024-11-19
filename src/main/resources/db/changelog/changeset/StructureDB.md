1. Таблица customer (Клиенты)
   id (PK, bigint, auto-increment) — Идентификатор клиента
   first_name (varchar) — Имя клиента
   last_name (varchar) — Фамилия клиента
   email (varchar, уникальный) — Электронная почта клиента
   phone (varchar) — Телефонный номер клиента
   address (varchar) — Адрес доставки
2. Таблица product (Товары)
   id (PK, bigint, auto-increment) — Идентификатор товара
   item_name (varchar) — Название товара
   description (text) — Описание товара
   price (decimal) — Цена товара
   quantity (int) — Количество товара на складе
   owner_id (bigint, foreign key) - владелец товара
3. Таблица orders (Заказы)
   id (PK, bigint, auto-increment) — Идентификатор заказа
   customer_id (FK, bigint) — Ссылка на клиента, который создал заказ
   total_amount (decimal) — Общая стоимость заказа
   status (varchar) — Статус заказа ("Cancelled", "Pending", "Paid", "Shipped", "Delivered")
   created_at (timestamp) — Дата и время создания заказа
   updated_at (timestamp) — Дата и время последнего обновления заказа
4. Таблица order_item (Товары в заказе)
   id (PK, bigint, auto-increment) — Идентификатор элемента заказа
   order_id (FK, bigint) — Ссылка на заказ
   product_id (FK, bigint) — Ссылка на товар
   quantity (int) — Количество единиц товара в заказе
   price (decimal) — Цена товара на момент заказа (может отличаться от текущей)
5. Таблица payment (Платежи)
   id (PK, bigint, auto-increment) — Идентификатор платежа
   order_id (FK, bigint) — Ссылка на заказ
   payment_method (varchar) — Метод оплаты (например, "Credit Card", "PayPal", "Bank Transfer")
   status (varchar) — Статус платежа (например, "Pending", "Completed", "Failed")
   amount (decimal) — Сумма платежа
   paid_at (timestamp) — Дата и время проведения платежа
6. Таблица shipping (Доставка)
   id (PK, bigint, auto-increment) — Идентификатор доставки
   order_id (FK, bigint) — Ссылка на заказ
   shipping_address (varchar) — Адрес доставки (может отличаться от адреса клиента)
   carrier (varchar) — Перевозчик (например, DHL, FedEx)
   tracking_number (varchar) — Номер для отслеживания
   status (varchar) — Статус доставки (например, "In Progress", "Delivered")
   shipped_at (timestamp) — Дата отправки
   delivered_at (timestamp) — Дата доставки
7. Таблица shopping_card (Корзина для товара)
   id (PK, bigint, auto-increment) - Идентификатор товара в корзине
   customer_id (foreign key, bigint) — Ссылка на клиента
   product_id (foreign key, bigint) - Ссылка на товар
   count (bigint) - количество добавленного товара

Ниже таблицы под вопросом ????????

8. Таблица inventory (Инвентарь)
   id (PK, bigint, auto-increment) — Идентификатор записи инвентаря
   product_id (FK, bigint) — Ссылка на товар
   quantity_in_stock (int) — Количество товара на складе
   last_updated_at (timestamp) — Дата последнего обновления информации об инвентаре
   Дополнительные таблицы (опционально):
   category (Категории товаров):

id (PK, bigint, auto-increment) — Идентификатор категории
name (varchar) — Название категории
Связь между таблицами product и category будет через внешние ключи для фильтрации товаров по категориям.

order_status_history (История изменения статусов заказов):

id (PK, bigint, auto-increment) — Идентификатор записи истории
order_id (FK, bigint) — Ссылка на заказ
old_status (varchar) — Старый статус
new_status (varchar) — Новый статус
changed_at (timestamp) — Время изменения статуса