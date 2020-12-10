import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import requests
import sys

import test_data

URL = 'http://traffic-tracker.herokuapp.com/planes/week'
DAYS = 7


def main():
    url = URL
    if len(sys.argv) > 1:
        url += '/' + sys.argv[1]
    date, total, avg_planes, planes_23, planes_0, avg_altitude, avg_speed, weekdays = fetch(url)
    # noinspection PyTypeChecker,PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b") + " - " + (date + timedelta(days=6))
              .strftime("%d. %b %Y"))

    plt.xlabel('Weekdays')
    # noinspection PyTypeChecker,PyUnresolvedReferences
    days = [(date + timedelta(days=i)).strftime("%a (%d.%m.)") for i in range(DAYS)]
    x_axis = [i for i in range(DAYS)]
    plt.xticks(x_axis, days)
    plt.gcf().autofmt_xdate()

    plt.ylabel('Planes')
    y_axis = [i for i in range(500) if i % 20 == 0]
    plt.yticks(y_axis)

    text1, text2 = set_texts(total, avg_altitude, avg_speed, avg_planes[0], planes_23, planes_0)
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(weekdays):
        if v == 0:
            continue
        plt.text(i - 0.05, v + .25, "{:,}".format(v))

    plt.bar(x_axis, weekdays, label="absolute")
    plt.plot(x_axis, avg_planes, label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch(url):
    # response = test_data.oct_week
    response = requests.get(url).json()
    print(response)
    date = datetime.strptime(response['start_date'], '%Y-%m-%d')
    avg_planes = [None] * DAYS
    for i, avg_plane in enumerate(response['avg_planes']):
        avg_planes[i] = avg_plane
    weekdays = [None] * DAYS
    for i, weekday in enumerate(response['weekdays']):
        weekdays[i] = weekday
    return date, int(response['total']), avg_planes, int(response['planes_23']), int(response['planes_0']), response[
        'avg_altitude'], response['avg_speed'], weekdays


def set_texts(total, avg_altitude, avg_speed, avg_planes, planes_23, planes_0):
    text1 = "Planes total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Planes avg: " + "{:,}".format(avg_planes) \
            + "\nPlanes after 23.00/0.00h: " + "{:,}".format(planes_23) + "/{:,}".format(planes_0)
    return text1, text2


if __name__ == "__main__":
    main()
