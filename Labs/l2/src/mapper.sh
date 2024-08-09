#!/bin/sh

awk -F "," '{printf "%s\t%s\n", $2, $3}'