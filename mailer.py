__author__ = 'adam'

import configparser
import logging
import argparse
import smtplib

from email.mime.text import MIMEText

from datetime import datetime, timedelta

from xlrd import open_workbook
from jinja2 import Template

template = Template("""
<p>Dear {{ instructor.first }},</p>

<p>One of the most common Blackboard Learn problems encountered by students and instructors is a student's inability to
    resume taking a quiz or test after a temporary network glitch. This is greatly exacerbated by the use of the "Force
    Completion" test option. We strongly recommend that you never use this option in your Bb quiz or test unless you
    have a very specific pedagogical reason to do so. If you are not familiar with the option, a more detailed
    explanation is available at <a href="https://bblearn.nau.edu/bbcswebdav/xid-28427315_1" target="_blank">this page</a>.
    If you are familiar with this option and would like to keep it in place regardless, please ignore the rest of this
    message.
</p>

<p>We have run a report to find tests and quizzes in your {{ term }} courses that have the Force Completion option
    selected. We <i>strongly</i> encourage you to disable this option and to use <b>Auto-Submit</b> instead. To turn off
    Force Completion for these items, simply find the item in your course (we have done our best to identify where that
    is), select <b>Edit the Test Options</b> from its drop-down menu, and under the <b>Test Availability</b> section,
    deselect/uncheck <b>Force Completion</b>, then click <b>Submit</b>. </p>

<p>{{ term }} tests with Force Completion enabled as of {{ day_before_report }}:</p>

<ul>
{% for course, tests in instructor.courses.items() %}
    <li> {{ course }}
        <ul>
            {% for test in tests %} <li> {{ test }} </li>
            {% endfor %}
        </ul>
        <br/>
    </li>
{% endfor %}
</ul>


<p>Please contact the e-Learning Center if you would like to discuss this setting. In short, we recommend that you never
    use the Force Completion option.</p>

<p>
    <a href="http://nau.edu/elc">e-Learning Center</a><br>
    <a href="mailto:elc-help@nau.edu">elc-help@nau.edu</a><br>
    In Flagstaff: +1 (928) 523-5554<br>
    Elsewhere: +1 (866) 802-5256<br>
</p>
""")


def create_root_logger(log_file):
    parent_logger = logging.getLogger('nau_force_completion')
    parent_logger.setLevel(logging.DEBUG)

    fh = logging.FileHandler(log_file)
    fh.setLevel(logging.DEBUG)

    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)

    formatter = logging.Formatter('%(asctime)s %(name)s^%(levelname)s: %(message)s')
    fh.setFormatter(formatter)
    ch.setFormatter(formatter)

    parent_logger.addHandler(fh)
    parent_logger.addHandler(ch)


def parse_cli_arguments():
    argparser = argparse.ArgumentParser(description='Force Completion email tool for ELC @ NAU.')

    argparser.add_argument('--config', required=True, help='Path to ini file.', metavar='FILE')

    argparser.add_argument('--report', required=True, metavar='FILE',
                           help='Path to the force completion report file.')

    argparser.add_argument('--dry-run', action='store_true',
                           help='Add this flag to send all emails to the default address specified in the ini file.')

    return vars(argparser.parse_args())

# parse CLI args for:
args = parse_cli_arguments()
# dry-run?
dry_run = args['dry_run']
# report file
report_file = args['report']
config_file = args['config']

# parse some report metadata from the filename
'force-completion-1154-2015-08-04_120920'
filename = report_file[report_file.index('force-completion-'):]
termcode = filename[17:21]
term = {'1': 'Spring', '4': 'Summer', '7': 'Fall', '8': 'Winter'}[termcode[3]] + ' 20' + termcode[1:3]

day_before_report = datetime.strptime(filename[22:32], '%Y-%m-%d') - timedelta(days=1)
day_before_report = day_before_report.strftime('%A %B %d, %Y')

# read configuration
config = configparser.ConfigParser()
config.read(config_file)
config = config['FORCE_COMPLETE']

# setup root logger
logfile = config.get('logfile', 'force-completion-mailer.log')
create_root_logger(logfile)
log = logging.getLogger('nau_force_completion.mailer')
log.debug("Parameters: %s", args)
log.debug("Config: %s", {k: config[k] for k in config})

# get default email
default_email = config.get('default_email')

# get server info
smtp_server = config['smtp_server']
smtp_port = config['smtp_port']

sender = smtplib.SMTP(host=smtp_server, port=smtp_port)

# parse report into instructors, courses and tests

report = open_workbook(filename=report_file).sheet_by_index(0)
header_keys = [report.cell(0, idx).value for idx in range(report.ncols)]

rows_as_dict_list = []
for row_index in range(1, report.nrows):
    d = {header_keys[col_index]: report.cell(row_index, col_index).value
         for col_index in range(report.ncols)}
    rows_as_dict_list.append(d)

instructors = {}
num_instructors = 0
num_courses = 0
num_tests = 0
while len(rows_as_dict_list) > 0:
    row = rows_as_dict_list.pop()

    uid = row['PI UID']
    first_name = row['PI First Name']
    last_name = row['PI Last Name']
    email = row['PI Email']

    course_id = row['Course ID']
    course_name = row['Course Name']
    test_name = row['Test Name']
    test_path = row['Path to Test']

    if uid not in instructors:
        instructors[uid] = {'first': first_name, 'last': last_name, 'email': email, 'courses': {}}
        num_instructors += 1

    if course_name not in instructors[uid]['courses']:
        instructors[uid]['courses'][course_name] = []
        num_courses += 1

    instructors[uid]['courses'][course_name].append(test_path + ' > ' + test_name)
    num_tests += 1

# remove the course id from the data structure, it's no longer needed for templating
for i in instructors:
    for c in instructors[i]['courses']:
        instructors[i]['courses'][c] = sorted(instructors[i]['courses'][c])


# print stats on report (num instructors, num courses, num tests)
log.info('Report successfully parsed.')
log.info('%s instructors found in report.', num_instructors)
log.info('%s courses found in report.', num_courses)
log.info('%s tests found in report.', num_tests)
log.info('Sending %s emails...', num_instructors)

emails_sent = 0
for uid in instructors:
    instructor = instructors.get(uid)

    current_email = template.render(instructor=instructor, term=term, day_before_report=day_before_report)
    msg = MIMEText(current_email, 'html')
    msg['Subject'] = 'Bb Learn Force Completion Notification'
    msg['From'] = 'e-Learning Center <elc-help@nau.edu>'

    to_addr = 'adam.perry@nau.edu'  # default_email if dry_run else instructor.get('email')

    instructor_name = instructor['first'] + ' ' + instructor['last']
    msg['To'] = instructor_name + ' <' + to_addr + '>'

    sender.sendmail(from_addr='elc-help@nau.edu', to_addrs=to_addr, msg=msg.as_string())
    emails_sent += 1
    log.info('Sent email to %s (%s), %s/%s sent.', instructor_name, to_addr, emails_sent, num_instructors)

sender.quit()
