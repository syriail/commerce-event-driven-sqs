CREATE TABLE IF NOT EXISTS orders (
    id UUID not null primary key,
    customer_id varchar not null,
    address jsonb not null,
    payment_id varchar,
    shipment_id varchar,
    total_price real not null,
    status varchar(20) not null,
    create_date timestamp with time zone not null
);

create table if not exists order_item (
    id UUID not null,
    order_id UUID not null,
    price real not null,
    quantity integer not null,
    primary key (id, order_id),
    CONSTRAINT fk_order_item_order_id
          FOREIGN KEY(order_id)
            REFERENCES orders(id)
);

create index customer_order_inx on orders (customer_id);
CREATE INDEX idx_customer_postcode ON orders USING HASH((address->'postalCode'));