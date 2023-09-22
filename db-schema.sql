create table airline
(
    id   bigserial not null,
    icao varchar(255),
    name varchar(255),
    primary key (id)
);
create table airport
(
    id     bigserial not null,
    icao   varchar(255),
    name   varchar(255),
    region varchar(255) check (region in ('INTERCONTINENTAL', 'INTERNATIONAL', 'NATIONAL', 'UNKNOWN')),
    primary key (id)
);
create table day
(
    abs_altitude                 integer   not null,
    abs_speed                    integer   not null,
    avg_altitude                 integer   not null,
    avg_speed                    integer   not null,
    date                         date,
    departures_continental       float4,
    departures_continental_abs   integer,
    departures_international     float4,
    departures_international_abs integer,
    departures_national          float4,
    departures_national_abs      integer,
    departures_unknown           float4,
    departures_unknown_abs       integer,
    flights0                     integer   not null,
    flights23                    integer   not null,
    less_than_thirty_flights     boolean   not null,
    total                        integer   not null,
    wind_speed                   float4    not null,
    id                           bigserial not null,
    hours_flight                 bytea,
    hours_wind                   bytea,
    primary key (id)
);
create table day_departures_top
(
    departures_top     integer,
    day_id             bigint       not null,
    departures_top_key varchar(255) not null,
    primary key (day_id, departures_top_key)
);
create table device
(
    id   bigserial not null,
    name varchar(255),
    pw   varchar(255),
    primary key (id)
);
create table flight
(
    altitude     integer   not null,
    photo_exists boolean   not null,
    speed        integer   not null,
    airline_id   bigint,
    date_time    timestamp(6),
    day_id       bigint,
    departure_id bigint,
    id           bigserial not null,
    photo_id     bigint unique,
    plane_id     bigint,
    callsign     varchar(255),
    primary key (id)
);
create table forecast_day
(
    date        date,
    probability float4    not null,
    id          bigserial not null,
    primary key (id)
);
create table forecast_hour
(
    probability float4,
    time        time(6),
    wind_degree integer   not null,
    hour_id     bigint,
    id          bigserial not null,
    primary key (id)
);
create table forecast_score
(
    mean_absolute_error float4    not null,
    precision           float4    not null,
    recall              float4    not null,
    id                  bigserial not null,
    confusion_matrix    bytea,
    primary key (id)
);
create table photo
(
    id  bigserial not null,
    arr bytea,
    primary key (id)
);
create table plane
(
    id   bigserial not null,
    icao varchar(255),
    primary key (id)
);
create table wind_day
(
    abs_wind       integer   not null,
    abs_wind_speed float4    not null,
    date           date,
    wind_speed     float4    not null,
    id             bigserial not null,
    hours_wind     bytea,
    primary key (id)
);
alter table if exists day_departures_top
    add constraint FK5q23q20fny9svqw82d0g7iosm foreign key (day_id) references day;
alter table if exists flight
    add constraint FK37wfh52g7g91rllg104gfq3yv foreign key (airline_id) references airline;
alter table if exists flight
    add constraint FK64eg0jepjnnfeeaxf0j8ftq6b foreign key (day_id) references day;
alter table if exists flight
    add constraint FKaxqek9h4f7km4qg67twbx2go5 foreign key (departure_id) references airport;
alter table if exists flight
    add constraint FK7kxluqoip4big7yw0x46jwib0 foreign key (photo_id) references photo;
alter table if exists flight
    add constraint FK7p9fvp6d7uh9cgn47uet8a8nb foreign key (plane_id) references plane;
alter table if exists forecast_hour
    add constraint FKcc2t0r98asl351eng2acqfrlq foreign key (hour_id) references forecast_day;
