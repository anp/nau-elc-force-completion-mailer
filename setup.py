__author__ = 'adam'

from setuptools import find_packages, setup

setup(name="nau-force-completion-mailer",
      version="0.1",
      author="Adam Perry",
      author_email='adam.perry@nau.edu',
      platforms=["any"],
      license="MIT",
      packages=find_packages(),
      install_requires=[
          "Jinja2=2.8",
          "xlrd=0.9.4"
      ],
      )
