import sys, json

cnt = 1

# simple JSON echo script
for line in sys.stdin:
  print line[:-1]
print cnt
cnt = cnt+1