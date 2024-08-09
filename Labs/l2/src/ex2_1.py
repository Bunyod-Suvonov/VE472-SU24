import time

import csv
import random


def generate_student_id():
    return ''.join([str(random.randint(0, 9)) for _ in range(10)])


def generate_num():
    return random.randint(0, 100)


def generate_grade():
    return random.randint(0, 100)

if __name__ == '__main__':
    first_name = open('/home/hadoopuser/ohyeahbaby/firstnames.txt', 'r')
    last_name = open('/home/hadoopuser/ohyeahbaby/lastnames.txt', 'r')
    students = []
    first = first_name.readline()
    first = first[:-1]
    last = last_name.readline()
    last = last[:-1]
    while first and last:
        students.append(first + " " + last)
        first = first_name.readline()
        first = first[:-1]
        last = last_name.readline()
        last = last[:-1]

    csv_data = []
    for i in range(len(students)):
        student = students[i]
        student_id = generate_student_id()
        appear_times = generate_num()
        for j in range(100):
            grade = generate_grade()
            csv_data.append([student, student_id, grade])

    random.shuffle(csv_data)

    write = open('studentsay2.csv', 'w', encoding='utf8', newline='')
    writer = csv.writer(write)
    writer.writerows(csv_data)

    first_name.close()
    last_name.close()
    write.close()