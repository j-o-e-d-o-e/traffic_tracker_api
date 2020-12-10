import matplotlib.pyplot as plt
from datetime import datetime
import requests
import sys

import test_data

URL = 'http://traffic-tracker.herokuapp.com/planes/year'
MONTHS = 12


def main():
    url = URL
    if len(sys.argv) > 1:
        url += '/' + sys.argv[1]
    date, total, avg_planes, planes_23, planes_0, avg_altitude, avg_speed, lt_thirty_planes, months = fetch(url)
    # noinspection PyTypeChecker,PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b") + " - 31. Dec " + str(date.year))

    plt.xlabel('Months')
    x_axis = [i for i in range(MONTHS)]
    plt.xticks(x_axis, ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"])
    plt.gcf().autofmt_xdate()

    plt.ylabel('Planes')
    y_axis = [i for i in range(7000) if i % 500 == 0]
    plt.yticks(y_axis)

    avg_plane = next(item for item in avg_planes if item is not None)
    text1, text2 = set_texts(total, avg_altitude, avg_speed, avg_plane, lt_thirty_planes, planes_23, planes_0)
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(months):
        if v == 0 or v is None:
            continue
        plt.text(i - 0.2, v + 2, v)

    plt.bar(x_axis, months, label="absolute")
    plt.plot(x_axis, avg_planes, label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch(url):
    # response = test_data.year_2019
    response = requests.get(url).json()
    print(response)
    avg_planes = [None] * MONTHS
    for i, avg_plane in enumerate(response['avg_planes']):
        avg_planes[i] = avg_plane
    months = [None] * MONTHS
    for i, month in enumerate(response['months']):
        months[i] = month
    print(months)
    return datetime.strptime(response['start_date'], '%Y-%m-%d'), int(response['total']), avg_planes, int(
        response['planes_23']), int(response['planes_0']), response['avg_altitude'], response['avg_speed'], response[
               'days_with_less_than_thirty_planes'], months


def set_texts(total, avg_altitude, avg_speed, avg_planes, lt_thirty_planes, planes_23, planes_0):
    text1 = "Planes total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Planes avg: " + "{:,}".format(avg_planes) \
            + "\nDays with < 30 planes: " + "{:,}".format(lt_thirty_planes) + "%" \
            + "\nPlanes after 23h/0h: " + "{:,}".format(planes_23) + "/{:,}".format(planes_0)
    return text1, text2


if __name__ == "__main__":
    main()
