create table if not exists item (
    id UUID not null primary key,
    price real not null,
    display_name varchar(50) not null,
    description varchar not null,
    quantity integer not null,
    reserved integer not null default 0,
    on_shelf integer not null,
    CONSTRAINT display_name_unique UNIQUE (display_name)
);