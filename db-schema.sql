-- noinspection SqlNoDataSourceInspectionForFile
drop table if exists flight;
drop table if exists day;
drop table if exists plane;
drop table if exists airline;
drop table if exists airport;

create table day (
    id  bigserial not null,
    abs_altitude int4,
    abs_speed int4,
    abs_wind int4,
    abs_wind_speed float4,
    avg_altitude int4,
    avg_speed int4,
    date date,
    departures_continental float4,
    departures_continental_abs int4,
    departures_international float4,
    departures_international_abs int4,
    departures_national float4,
    departures_national_abs int4,
    departures_unknown float4,
    departures_unknown_abs int4,
    flights0 int4,
    flights23 int4,
    hours_flight bytea,
    hours_wind bytea,
    less_than_thirty_flights boolean,
    total int4,
    wind_speed float4,
    primary key (id)
);

create table airline (
    id  bigserial not null,
    icao varchar(255),
    name varchar(255),
    primary key (id)
);

create table plane (
    id  bigserial not null,
    icao varchar(255),
    primary key (id)
);

create table airport (
    id  bigserial not null,
    icao varchar(255),
    name varchar(255),
    region varchar(255),
    primary key (id)
);

create table flight (
    id  bigserial not null,
    altitude int4 not null,
    callsign varchar(255),
    date_time timestamp,
    photo bytea,
    speed int4 not null,
    airline_id int8,
    day_id int8,
    departure_id int8,
    plane_id int8,
    primary key (id)
);
alter table flight
    add constraint FK37wfh52g7g91rllg104gfq3yv foreign key (airline_id) references airline;
alter table flight
    add constraint FK64eg0jepjnnfeeaxf0j8ftq6b foreign key (day_id) references day;
alter table flight
    add constraint FK7p9fvp6d7uh9cgn47uet8a8nb foreign key (plane_id) references plane;
alter table flight
    add constraint FKaxqek9h4f7km4qg67twbx2go5 foreign key (departure_id) references airport;



drop table if exists forecast_hour;
drop table if exists forecast_day;

create table forecast_day
(
    id  bigserial not null,
    date date,
    probability float4,
    primary key (id)
);
create table forecast_hour
(
    id  bigserial not null,
    probability float4,
    time time,
    wind_degree int4,
    hour_id int8,
    primary key (id)
);
alter table forecast_hour
    add constraint FKcc2t0r98asl351eng2acqfrlq foreign key (hour_id) references forecast_day;

drop table if exists forecast_score;

create table forecast_score
(
    id  bigserial not null,
    confusion_matrix bytea,
    mean_absolute_error float4,
    precision float4,
    primary key (id)
);

drop table if exists day_departures_top;

create table day_departures_top
(
    day_id int8 not null,
    departures_top int4,
    departures_top_key varchar(255) not null,
    primary key (day_id, departures_top_key)
);
alter table day_departures_top
    add constraint FK5q23q20fny9svqw82d0g7iosm foreign key (day_id) references day;

create table device (
    id  bigserial not null,
    name varchar(255),
    pw varchar(255),
    primary key (id)
);
