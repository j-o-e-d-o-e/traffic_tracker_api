import matplotlib.pyplot as plt
from datetime import datetime, timedelta
from calendar import monthrange
import requests
import sys

URL = 'http://traffic-tracker.herokuapp.com/planes/month'
DAYS = 0
date = None
now = None
date_string = ''

total = 0
avg_planes = 0
planes_23 = 0
planes_0 = 0
avg_altitude = 0
avg_speed = 0
days_with_less_than_thirty_planes = 0
days = [0] * DAYS

text1 = ''
text2 = ''


def main():
    fetch()

    # noinspection PyTypeChecker,PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b") + " - " + (date + timedelta(days=DAYS - 1))
              .strftime("%d. %b %Y"))

    plt.xlabel('Days')
    x_axis = [i for i in range(DAYS)]
    day_labels = []
    for i in range(DAYS):
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

    set_text()
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(days):
        plt.text(i - 0.25, v + 2, v)

    plt.bar(x_axis, days, label="absolute")
    if now.date() >= date.date() + timedelta(days=DAYS):
        plt.plot(x_axis, [avg_planes] * DAYS, label="average", linestyle="--", color="orange")
    else:
        days_with_data = now.date().day - date.date().day + 1
        plt.plot(x_axis, [avg_planes] * days_with_data + [None] * (DAYS - days_with_data),
                 label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch():
    global URL, date, DAYS, total, avg_planes, planes_23, planes_0, avg_altitude, avg_speed, \
        days_with_less_than_thirty_planes, days, now
    if date_string != '':
        URL = URL + '/' + date_string
    response = requests.get(URL).json()
    print(response)

    date = datetime.strptime(response['start_date'], '%Y-%m-%d')
    DAYS = monthrange(date.year, date.month)[1]
    now = datetime.strptime(response['now'], '%Y-%m-%dT%H:%M:%S.%f')

    total = int(response['total'])
    avg_planes = int(response['avg_planes'])
    planes_23 = int(response['planes_23'])
    planes_0 = int(response['planes_0'])
    avg_altitude = response['avg_altitude']
    avg_speed = response['avg_speed']
    days_with_less_than_thirty_planes = response['days_with_less_than_thirty_planes']

    days = response['days']


def set_text():
    global text1, text2
    text1 = "Planes total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Planes avg: " + "{:,}".format(avg_planes) \
            + "\nDays with < 30 planes: " + "{:,}".format(days_with_less_than_thirty_planes) + "%" \
            + "\nPlanes after 23.00/0.00h: " + "{:,}".format(planes_23) + "/{:,}".format(planes_0)


def set_date():
    global date_string
    date_string = sys.argv[1]


if __name__ == "__main__":
    if len(sys.argv) > 1:
        set_date()
    main()
