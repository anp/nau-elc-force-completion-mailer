# nau-elc-force-completion-mailer
SMTP tool to mail custom template-driven emails to a variety of users.

```
usage: mailer.py [-h] --config FILE --report FILE [--dry-run]

Force Completion email tool for ELC @ NAU.

optional arguments:
  -h, --help     show this help message and exit
  --config FILE  Path to ini file.
  --report FILE  Path to the force completion report file.
  --dry-run      Add this flag to send all emails to the default address
                 specified in the ini file.
```

The only currently supported auth for the SMTP server is trusted connections from a local subnet.

The report generated should match exactly the Force Completion report from https://github.com/dikaiosune/nau-bb-learn-reporting.

The default email address is the recipient when a dry-run is chosen.
