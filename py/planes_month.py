import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import requests
import sys

import test_data

URL = 'http://traffic-tracker.herokuapp.com/planes/month'


def main():
    url = URL
    if len(sys.argv) > 2:
        url += '/' + sys.argv[1] + '/' + sys.argv[2]
    date, total, avg_planes, planes_23, planes_0, avg_altitude, avg_speed, lt_thirty_planes, days = fetch(url)

    # noinspection PyTypeChecker,PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b") + " - " + (date + timedelta(days=len(days) - 1))
              .strftime("%d. %b %Y"))

    plt.xlabel('Days')
    x_axis = [i for i in range(len(days))]
    day_labels = []
    for i in range(len(days)):
        if i % 2 == 0:
            # noinspection PyUnresolvedReferences,PyTypeChecker
            day = (date + timedelta(days=i)).strftime("%a (%d.%m.)")
            day_labels.append(day)
        else:
            day_labels.append("")
    plt.xticks(x_axis, day_labels)
    plt.gcf().autofmt_xdate()

    plt.ylabel('Planes')
    y_axis = [i for i in range(500) if i % 20 == 0]
    plt.yticks(y_axis)

    text1, text2 = set_text(total, avg_altitude, avg_speed, avg_planes[0], lt_thirty_planes, planes_23, planes_0)
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(days):
        if v == 0:
            continue
        plt.text(i - 0.25, v + 0.25, v)

    plt.bar(x_axis, days, label="absolute")
    plt.plot(x_axis, avg_planes, label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch(url):
    # response = test_data.dec
    response = requests.get(url).json()
    print(response)
    date = datetime.strptime(response['start_date'], '%Y-%m-%d')
    month_len = len(response['days'])
    avg_planes = [None] * month_len
    for i, avg_plane in enumerate(response['avg_planes']):
        avg_planes[i] = avg_plane
    days = [None] * month_len
    for i, day in enumerate(response['days']):
        days[i] = day
    return date, int(response['total']), avg_planes, int(response['planes_23']), int(response['planes_0']), response[
        'avg_altitude'], response['avg_speed'], response['days_with_less_than_thirty_planes'], days


def set_text(total, avg_altitude, avg_speed, avg_planes, days_with_less_than_thirty_planes, planes_23, planes_0):
    text1 = "Planes total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Planes avg: " + "{:,}".format(avg_planes) \
            + "\nDays with < 30 planes: " + "{:,}".format(days_with_less_than_thirty_planes) + "%" \
            + "\nPlanes after 23h/0h: " + "{:,}".format(planes_23) + "/{:,}".format(planes_0)
    return text1, text2


if __name__ == "__main__":
    main()
