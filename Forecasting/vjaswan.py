#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov  8 22:04:52 2018

@author: vidhishajaswani
"""

import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
from statsmodels.graphics.tsaplots import plot_pacf
from statsmodels.tsa.ar_model import AR
import statsmodels.api as sm
from scipy.stats import chisquare
from math import sqrt
import numpy as np

#read and split data into test and train
data=pd.read_csv("vjaswan.csv",header=None)
data.columns=['X1']
train=data[0:1500]
test=data[1500:]

#------TASK 1-----------
#plot for all data points
data.X1.plot(title= 'Time Series Data')
plt.show()

#applying log transformation
logX1=np.log(data.X1)
logX1.plot(title='Log of Time Series Data')
plt.show()

#------TASK 2-----------
#simple moving average on train dataset for varying k
rmse_t2=list()
window_t2=list()
for k in range(2,50):
    temp_t2=train.copy()
    temp_t2['forecast'] = train['X1'].rolling(window=k).mean() #simple moving average
    temp_t2=temp_t2.iloc[k:,]   #since first k values become NAN for window size k
    rmse_t2.append(sqrt(mean_squared_error(temp_t2.X1, temp_t2['forecast'])))
    window_t2.append(k)
 
#plot RMSE versus k
plt.plot(window_t2,rmse_t2)
plt.xlabel("Window Size")
plt.ylabel("RMSE")
plt.title("RMSE versus Window Size(k)")
plt.show()

minWindow=window_t2[rmse_t2.index(min(rmse_t2))]
print("Minimum RMSE is %s and is obtained at k= %s" % (min(rmse_t2), minWindow))

#plot actual versus predicted for best k
temp_t2=train.copy()
temp_t2['forecast'] = train['X1'].rolling(window=minWindow).mean()
temp_t2=temp_t2.iloc[minWindow:,]
plt.plot(temp_t2.X1,label="Actual")
plt.plot(temp_t2['forecast'],label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for SMA for the best value of k")
plt.show()

#zoomed in plot for the same
plt.plot(temp_t2.X1[450:500],label="Actual")
plt.plot(temp_t2.forecast[450:500],label="Predicted")
plt.legend(loc='best')
plt.title("Zoomed Plot: Actual versus Predicted for SMA for the best value of k")
plt.show()


#------TASK 3-----------
#exponential smoothening model
rmse_t3=list()
aVals_t3=list()
temp_t3=train.copy()
a=1
while a<10:
    temp_t3=train.copy()
    temp_t3['forecast']= train['X1'].ewm(alpha=a/10, adjust=False).mean()
    rmse_t3.append(sqrt(mean_squared_error(temp_t3.X1, temp_t3['forecast'])))
    aVals_t3.append(a/10)
    a=a+1
    

minAVal=(aVals_t3[rmse_t3.index(min(rmse_t3))])    
print("Minimum RMSE is %s and is obtained at a= %s " % (min(rmse_t3),minAVal))


#plot for a-values versus RMSE
plt.plot(aVals_t3,rmse_t3)
plt.xlabel("a-values")
plt.ylabel("RMSE")
plt.title("a-values versus RMSE")
plt.show()

#plot actual versus predicted for best a
temp_t3=train.copy()
temp_t3['forecast']= train['X1'].ewm(alpha=minAVal, adjust=False).mean()    
plt.plot(train['X1'],label="Actual")
plt.plot(temp_t3['forecast'],label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for ESM for the best value of a")
plt.show()

#zoomed in plot for actual versus predicted for best a
plt.plot(train.X1[450:500],label="Actual")
plt.plot(temp_t3.forecast[450:500],label="Predicted")
plt.title("Zoomed Plot: Actual versus Predicted for ESM for the best value of a")
plt.show()

#------TASK 4-----------
#AR model
rmse_t4=list()
aVals_t4=list()
temp_t4=train.copy()
plot_pacf(temp_t4,lags=30)
plt.show()

model = AR(temp_t4)
model_fit = model.fit(maxlag=2)
predictions = model_fit.predict(start=2, end=len(train), dynamic=False)

print('Lag: %s' % model_fit.k_ar)
print('Coefficients: %s' % model_fit.params)
temp_t4=temp_t4[1:]
rmse=sqrt(mean_squared_error(temp_t4.X1, predictions))
print('RMSE for the AR(2) model: %s' %rmse)

#plot for actual versus predicted
plt.plot(temp_t4.X1,label="Actual")
plt.plot(predictions,label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for AR")
plt.show()

#zoomed in plot for the same
plt.plot(temp_t4.X1[450:500],label="Actual")
plt.plot(predictions[450:500],label="Predicted")
plt.legend(loc='best')
plt.title("Zoomed Plot: Actual versus Predicted for AR")
plt.show()

#qq plot for residuals
residuals=temp_t4.X1-predictions
sm.qqplot(residuals, line='45')
plt.title("Q-Q Plot of Residuals")
plt.show()


#histogram for residuals
residuals=residuals.dropna()
plt.hist(residuals)
plt.title("Histogram for Residuals")
plt.show()

#chi-square test
print(chisquare(residuals))

#scatter plot of predicted versus residuals 
plt.scatter(predictions[0:1498],residuals[0:])
plt.xlabel("Predictions")
plt.ylabel("Residuals")
plt.title("Predictions versus Residuals")
plt.show()


#------TASK 5-----------

# Simple Moving Average for Test Data
temp_t5_sa=data.copy()
temp_t5_sa['forecast'] = temp_t5_sa['X1'].rolling(window=minWindow).mean()
temp_t5_sa=temp_t5_sa.iloc[1500+minWindow:,]
rmse_t5_sa=sqrt(mean_squared_error(temp_t5_sa.X1, temp_t5_sa['forecast']))
print('RMSE for the SMA model on test data: %s' %rmse_t5_sa)

    
#plot for actual versus predicted
plt.plot(temp_t5_sa.X1,label="Actual")
plt.plot(temp_t5_sa['forecast'],label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for SMA for the best value of k for Test Set")
plt.show()

# Exponential Model for Test Data
temp_t5_ewm=data.copy()
temp_t5_ewm['forecast']= temp_t5_ewm['X1'].ewm(alpha=minAVal, adjust=False).mean()
temp_t5_ewm=temp_t5_ewm[1500:2000]
rmse_t5_ewm=sqrt(mean_squared_error(temp_t5_ewm.X1, temp_t5_ewm.forecast))
print('RMSE for the ESM model on test data: %s' %rmse_t5_ewm)

#plot for actual versus predicted  
plt.plot(temp_t5_ewm['X1'],label="Actual")
plt.plot(temp_t5_ewm['forecast'],label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for ESM for the best value of a for Test Set")
plt.show()


# AR for Test Data
temp_t5_ar=test.copy()
plot_pacf(temp_t5_ar,lags=30)
plt.show()


window = model_fit.k_ar  #using same model on which it was trained
coef = model_fit.params  #using same model on which it was trained
predictions_t5 = []
temp_t5_ar=[]
for i in range(len(test)):
    predictions_t5.append(coef[0]+coef[1]*data.iloc[(1501+i)-2].X1+coef[2]*data.iloc[(1501+i)-1].X1+np.random.randn())
    temp_t5_ar.append(test.iloc[i].X1)



print('Lag: %s' % model_fit.k_ar)
print('Coefficients: %s' % model_fit.params)
#temp_t5_ar=temp_t5_ar[1:]
rmse_t5_ar=sqrt(mean_squared_error(temp_t5_ar, predictions_t5))
print('RMSE for the AR model on test data: %s' %rmse_t5_ar)

#actual versus predicted
plt.plot(temp_t5_ar,label="Actual")
plt.plot(predictions_t5,label="Predicted")
plt.legend(loc='best')
plt.title("Actual versus Predicted for AR for Test Data")
plt.show()







    










