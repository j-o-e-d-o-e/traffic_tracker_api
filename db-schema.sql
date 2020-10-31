-- noinspection SqlNoDataSourceInspectionForFile
drop table if exists plane;
drop table if exists day;

create table day
(
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
    hours_plane bytea,
    hours_wind bytea,
    less_than_thirty_planes boolean,
    planes0 int4,
    planes23 int4,
    total int4,
    wind_speed float4,
    primary key (id)
  );
create table plane
(
    id  bigserial not null,
    airline varchar(255),
    altitude int4,
    callsign varchar(255),
    date timestamp,
    departure_airport varchar(255),
    departure_airport_name varchar(255),
    icao varchar(255),
    speed int4,
    day_id int8,
    primary key (id)
);
alter table plane
    add constraint FKjhoo1xphjapbq7mr33orrvhfq foreign key (day_id) references day;

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
