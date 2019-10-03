import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import requests
import sys

URL = 'http://traffic-tracker.herokuapp.com/planes/week'
DAYS = 7
date = None
date_string = ''

total = 0
avg_planes = 0
planes_23 = 0
planes_0 = 0
avg_altitude = 0
avg_speed = 0
weekdays = [0] * DAYS

text1 = ''
text2 = ''


def main():
    fetch()
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

    set_text()
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(weekdays):
        plt.text(i - 0.05, v + 2, "{:,}".format(v))

    plt.bar(x_axis, weekdays, label="absolute")
    if datetime.now().date() >= date.date() + timedelta(days=DAYS):
        plt.plot(x_axis, [avg_planes] * DAYS, label="average", linestyle="--", color="orange")
    else:
        days_with_data = (datetime.now().date() - date.date() + timedelta(days=1)).days
        plt.plot(x_axis, [avg_planes] * days_with_data + [None] * (DAYS - days_with_data),
                 label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch():
    global URL, date, total, avg_planes, planes_23, planes_0, avg_altitude, avg_speed, weekdays
    if date_string != '':
        URL = URL + '/' + date_string
    response = requests.get(URL).json()
    print(response)

    date = datetime.strptime(response['start_date'], '%Y-%m-%d')

    total = int(response['total'])
    avg_planes = int(response['avg_planes'])
    planes_23 = int(response['planes_23'])
    planes_0 = int(response['planes_0'])
    avg_altitude = response['avg_altitude']
    avg_speed = response['avg_speed']

    weekdays = response['weekdays']


def set_text():
    global text1, text2
    text1 = "Planes total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Planes avg: " + "{:,}".format(avg_planes) \
            + "\nPlanes after 23.00/0.00h: " + "{:,}".format(planes_23) + "/{:,}".format(planes_0)


def set_date():
    global date_string
    date_string = sys.argv[1]


if __name__ == "__main__":
    if len(sys.argv) > 1:
        set_date()
    main()
