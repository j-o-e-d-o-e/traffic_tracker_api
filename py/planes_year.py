import matplotlib.pyplot as plt
from datetime import datetime
import requests
import sys

import test_data

URL = 'http://traffic-tracker.herokuapp.com/api/years'
MONTHS = 12


def main():
    url = URL
    if len(sys.argv) > 1:
        url += '/' + sys.argv[1]
    date, total, avg_flights, flights_23, flights_0, avg_altitude, avg_speed, lt_thirty_flights, months = fetch(url)
    # noinspection PyTypeChecker,PyUnresolvedReferences
    plt.title("TRAFFIC VOLUME\n" + date.strftime("%d. %b") + " - 31. Dec " + str(date.year))

    plt.xlabel('Months')
    x_axis = [i for i in range(MONTHS)]
    plt.xticks(x_axis, ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"])
    plt.gcf().autofmt_xdate()

    plt.ylabel('Flights')
    y_axis = [i for i in range(7000) if i % 500 == 0]
    plt.yticks(y_axis)

    avg_flight = next(item for item in avg_flights if item is not None)
    text1, text2 = set_texts(total, avg_altitude, avg_speed, avg_flight, lt_thirty_flights, flights_23, flights_0)
    plt.gcf().text(0.12, 0.9, text1, fontweight="bold")
    plt.gcf().text(0.24, 0.9, text2, fontweight="bold")

    for i, v in enumerate(months):
        if v == 0 or v is None:
            continue
        plt.text(i - 0.2, v + 2, v)

    plt.bar(x_axis, months, label="absolute")
    plt.plot(x_axis, avg_flights, label="average", linestyle="--", color="orange")
    plt.legend()
    plt.show()


def fetch(url):
    # response = test_data.year_2019
    response = requests.get(url).json()
    print(response)
    avg_flights = [None] * MONTHS
    for i, avg_flight in enumerate(response['avg_flights']):
        avg_flights[i] = avg_flight
    months = [None] * MONTHS
    for i, month in enumerate(response['months']):
        months[i] = month
    print(months)
    return datetime.strptime(response['start_date'], '%Y-%m-%d'), int(response['total']), avg_flights, int(
        response['flights_23']), int(response['flights_0']), response['avg_altitude'], response['avg_speed'], response[
               'days_with_less_than_thirty_flights'], months


def set_texts(total, avg_altitude, avg_speed, avg_flights, lt_thirty_flights, flights_23, flights_0):
    text1 = "Flights total: " + "{:,}".format(total) \
            + "\nAvg altitude: " + "{:,}".format(avg_altitude) + " m" \
            + "\nAvg speed: " + "{:,}".format(avg_speed) + " km/h"
    text2 = "Flights avg: " + "{:,}".format(avg_flights) \
            + "\nDays with < 30 flights: " + "{:,}".format(lt_thirty_flights) + "%" \
            + "\nFlights after 23h/0h: " + "{:,}".format(flights_23) + "/{:,}".format(flights_0)
    return text1, text2


if __name__ == "__main__":
    main()
