import sys, os

print sys.argv[1] # prints file to be ingested
print sys.argv[2] # prints collection id

print("python ingestion completed")
sys.exit(os.EX_OK) # code 0, all ok