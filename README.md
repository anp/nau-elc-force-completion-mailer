# nau-elc-force-completion-mailer
SMTP tool to mail custom template-driven emails to a variety of users.

Usage:
```
gradle clean capsule
java -jar build/libs/nau-elc-force-completion-mailer-1.0-capsule.jar smtp.server.addr:port /path/to/force/completion/report.tsv default.address@mail.something [--dry-run]
```

The only currently supported auth for the SMTP server is trusted connections from a local subnet.

The report generated should match exactly the Force Completion report from https://github.com/dikaiosune/nau-bb-learn-reporting.

The default email address is the recipient when a dry-run is chosen, and is also BCC'd when it's a live-run.
