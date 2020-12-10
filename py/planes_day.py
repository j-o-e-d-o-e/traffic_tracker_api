import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import requests
import sys

import test_data

URL = 'http://traffic-tracker.herokuapp.com/planes/day'
HOURS = 24


def main():
    url = URL
    if len(sys.argv) > 1:
        url += '/' + sys.argv[1]
    date, total, avg_altitude, avg_speed, wind_speed, hours_plane, avg_planes, hours_wind = fetch(url)
    fig, ax1 = plt.subplots()
    ax2 = ax1.twinx()
    ax1.grid()

    # noinspection PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b %Y (%a)"))

    ax1.set_xlabel('Hours')
    x_axis = [i for i in range(HOURS)]
    hours = []
    for i in range(HOURS):
        if i % 2 == 0:
            # noinspection PyUnresolvedReferences,PyTypeChecker
            hour = (date + timedelta(hours=i)).strftime("%H:%M")
            hours.append(hour)
        else:
            hours.append("")
    plt.xticks(x_axis, hours)
    plt.gcf().autofmt_xdate()

    text_planes, text_weather = set_texts(total, avg_altitude, avg_speed, wind_speed)
    plt.gcf().text(0.12, 0.9, text_planes, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text_weather, fontweight="bold")

    ax1.set_ylabel('Planes')
    line1, = ax1.plot(x_axis, hours_plane)
    line2, = ax1.plot(x_axis, avg_planes, linestyle="--", color="orange")

    ax2.set_ylabel('Wind direction')
    line3, = ax2.plot(x_axis, hours_wind, linestyle=":", color="r")
    ax2.set_yticks([i for i in range(360) if i % 50 == 0])

    plt.legend((line1, line2, line3), ('absolute', 'avg (5.45-0.00h)', 'wind dir'), loc=2)
    plt.show()


def fetch(url):
    # response = test_data.oct_10
    response = requests.get(url).json()
    print(response)
    hours_plane = [None] * HOURS
    for i, hour_plane in enumerate(response['hours_plane']):
        hours_plane[i] = hour_plane
    avg_planes = [None] * HOURS
    for i, avg_plane in enumerate(response['avg_planes']):
        avg_planes[i] = avg_plane
    hours_wind = [None] * HOURS
    for i, hour_wind in enumerate(response['hours_wind']):
        hours_wind[i] = hour_wind
    return datetime.strptime(response['date'], '%Y-%m-%d'), int(response['total']), response['avg_altitude'], response[
        'avg_speed'], response['wind_speed'], hours_plane, avg_planes, hours_wind


def set_texts(total, avg_altitude, avg_speed, wind_speed):
    text_planes = "Planes total: " + "{:,}".format(total) \
                  + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
                  + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text_weather = "\nWind speed: " + "{:,}".format(wind_speed) + " km/h"
    return text_planes, text_weather


if __name__ == "__main__":
    main()
