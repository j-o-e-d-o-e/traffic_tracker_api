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
    altitude int4,
    date timestamp,
    icao varchar(255),
    speed int4,
    day_id int8,
    primary key (id)
);
alter table plane
    add constraint FKjhoo1xphjapbq7mr33orrvhfq foreign key (day_id) references day;
