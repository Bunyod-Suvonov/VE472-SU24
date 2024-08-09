import time
from pyspark.sql import SparkSession
from pyspark.ml.feature import VectorAssembler, PCA
from pyspark.ml.regression import LinearRegression
from pyspark.ml.evaluation import RegressionEvaluator
import matplotlib.pyplot as plt

spark = SparkSession.builder.master("local[8]") \
                    .appName("PCA and Gradient Descent") \
                    .config("spark.executor.memory", "6g") \
                    .config("spark.executor.cores", "8") \
                    .getOrCreate()

start_time = time.time()

read_start = time.time()
df = spark.read.csv("file:///home/hadoopuser/ve472/normed.csv", header=True, inferSchema=True).dropna()
read_end = time.time()
print(f"Data Read Time: {read_end - read_start} seconds")

fill_start = time.time()
df = df.fillna(0)
fill_end = time.time()
print(f"Null Fill Time: {fill_end - fill_start} seconds")

feature_cols = df.columns
feature_cols.remove('year')

assemble_start = time.time()
assembler = VectorAssembler(inputCols=feature_cols, outputCol="features")
df_assembled = assembler.transform(df)
assemble_end = time.time()
print(f"Data Assemble Time: {assemble_end - assemble_start} seconds")

pca_start = time.time()
pca = PCA(k=4, inputCol="features", outputCol="pcaFeatures")
pca_model = pca.fit(df_assembled)
df_pca = pca_model.transform(df_assembled)
pca_end = time.time()
print(f"PCA Time: {pca_end - pca_start} seconds")

split_start = time.time()
train_data, test_data = df_pca.randomSplit([0.8, 0.2], seed=111)
split_end = time.time()
print(f"Data Split Time: {split_end - split_start} seconds")

train_start = time.time()
lr = LinearRegression(featuresCol='pcaFeatures', labelCol='year', maxIter=1000, regParam=0.3, elasticNetParam=0.8)
lr_model = lr.fit(train_data)
train_end = time.time()
print(f"Model Training Time: {train_end - train_start} seconds")

predict_start = time.time()
predictions = lr_model.transform(test_data)
predict_end = time.time()
print(f"Prediction Time: {predict_end - predict_start} seconds")

evaluate_start = time.time()
evaluator = RegressionEvaluator(labelCol="year", predictionCol="prediction", metricName="rmse")
rmse = evaluator.evaluate(predictions)
evaluate_end = time.time()
print(f"Evaluation Time: {evaluate_end - evaluate_start} seconds")

predictions_pd = predictions.select("year", "prediction").toPandas()

plt.figure(figsize=(10, 6))
plt.scatter(predictions_pd['year'], predictions_pd['prediction'], alpha=0.5)
plt.plot([predictions_pd['year'].min(), predictions_pd['year'].max()],
         [predictions_pd['year'].min(), predictions_pd['year'].max()],
         color='red', linestyle='--', linewidth=2)
plt.xlabel('Actual Year')
plt.ylabel('Predicted Year')
plt.title('Actual vs Predicted Year')
plt.show()

spark.stop()

end_time = time.time()
print(f"Total Time: {end_time - start_time} seconds")

print(f"Root Mean Squared Error (RMSE) on test data = {rmse}")


