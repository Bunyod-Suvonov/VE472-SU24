# MileStone 0

Use the following code to mount `MSD` from FOCS server:

```shell
sudo -S sshfs /home/hadoopuser/ece472 -o allow_other -o Port=2223 ece472@focs.ji.sjtu.edu.cn: -o IdentityFile=~/.ssh/id_rsa
sudo -S mount /home/hadoopuser/ece472/millionsong.iso /home/hadoopuser/ece472/
```

See `avroSpark.py` to retrieve data from FOCS.

See `testAvro.ipynb` for basic analysis.

For parallelization on `spark-submit` in local mode:

```shell
spark-submit --master local[12] --conf spark.pyspark.driver.python=python3 \
--conf spark.pyspark.python=python3 --driver-cores 2 --driver-memory 8g \
--executor-cores 4 --num-executors 10 --executor-memory 8g pyspark.py
```
