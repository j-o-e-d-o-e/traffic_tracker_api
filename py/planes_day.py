import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import requests
import sys

URL = 'http://traffic-tracker.herokuapp.com/planes/day'
HOURS = 24
date = None
date_string = ''

total = 0
avg_altitude = 0
avg_speed = 0
hours_plane = [None] * HOURS
avg_planes = [None] * HOURS
text_planes = ''

wind_speed = 0
hours_wind = [None] * HOURS
text_weather = ''

fig = None


def main():
    global fig
    fetch()
    fig, ax1 = plt.subplots()
    ax2 = ax1.twinx()
    ax1.grid()

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

    set_text()
    plt.gcf().text(0.12, 0.9, text_planes, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text_weather, fontweight="bold")

    ax1.set_ylabel('Planes')
    ax1.set_yticks([i for i in range(40) if i % 4 == 0])
    line1, = ax1.plot(x_axis, hours_plane)
    line2, = ax1.plot(x_axis, avg_planes, linestyle="--", color="orange")

    ax2.set_ylabel('Wind direction')
    line3, = ax2.plot(x_axis, hours_wind, linestyle=":", color="r")
    ax2.set_yticks([1, 2, 3, 4, 5, 6, 7, 8])
    ax2.set_yticklabels(['', 'W (270°)', '', 'S (180°)', '', 'E (90°)', '', 'N (0°)'])

    plt.legend((line1, line2, line3), ('absolute', 'avg (5.45-0.00h)', 'wind dir'), loc=2)
    plt.show()


def fetch():
    global URL, date, total, avg_altitude, avg_speed, wind_speed, hours_plane, avg_planes, hours_wind
    if date_string != '':
        URL = URL + '/' + date_string
    response = requests.get(URL).json()
    print(response)

    date = datetime.strptime(response['date'], '%Y-%m-%d')

    total = int(response['total'])
    avg_altitude = response['avg_altitude']
    avg_speed = response['avg_speed']

    wind_speed = response['wind_speed']

    now = datetime.now()
    if now.date() == date.date():
        current_data(now, response)
    else:
        avg_planes = [None] * 6 + [response['avg_planes']] * 18
        hours_plane = response['hours_plane']
        hours_wind = [None] * 6 + [get_wind_dir(hour_wind) for hour_wind in response['hours_wind'][6:]]


def current_data(now, response):
    global avg_planes, hours_plane, hours_wind
    if now.time().hour >= 6:
        hours = now.time().hour - 5
        avg_planes = [None] * 6 + [response['avg_planes']] * hours + [None] * (18 - hours)
    else:
        avg_planes = [None] * HOURS

    for i, hour_plane in enumerate(response['hours_plane']):
        if now.time().hour >= i:
            hours_plane[i] = hour_plane

    for i, hour_wind in enumerate(response['hours_wind'][6:]):
        if now.time().hour >= i + 6:
            hours_wind[i + 6] = get_wind_dir(hour_wind)


def get_wind_dir(deg):
    if deg == 0:
        return None
    if deg > 337.5 or deg < 22.5:
        return 8  # N
    if 22.5 < deg < 67.5:
        return 7  # NE
    if 67.5 < deg < 112.5:
        return 6  # E
    if 112.5 < deg < 157.5:
        return 5  # SE
    if 157.5 < deg < 202.5:
        return 4  # S
    if 202.5 < deg < 247.5:
        return 3  # SW
    if 247.5 < deg < 292.5:
        return 2  # W
    if 292.5 < deg < 337.5:
        return 1  # NW


def set_text():
    global text_planes, text_weather
    text_planes = "Planes total: " + "{:,}".format(total) \
                  + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
                  + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text_weather = "\nWind speed: " + "{:,}".format(wind_speed) + " km/h"


def set_date():
    global date_string
    date_string = sys.argv[1]


if __name__ == "__main__":
    if len(sys.argv) > 1:
        set_date()
    main()
