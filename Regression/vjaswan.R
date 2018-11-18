library(outliers)
library(corrplot)
library(ggplot2)
library(nortest)


#read data
data<-read.csv("vjaswan.csv")
#updated column names
colnames(data)<-c('X1','X2','X3','X4','X5','Y')

#-------------TASK 1-----------
#Task 1.1: hist, mean, variance for each Xi
means<-c()
variance<-c()
for(i in 1:5)
{
  col=colnames(data)[i]
  hist(data[,col], xlab=col, main=paste("Histogram for",col,sep=" "))
  means[i]=mean(data[,col])
  variance[i]=var(data[,col])
  
}
print("Means:")
print(means)
print("Variance:")
print(variance)

#Task 1.2: remove outliers
for(i in 1:5)
{
  outlier<-c()
  col=colnames(data)[i]
  outlier=boxplot(data[,col],main=paste("Box plot before removing outliers for ",col))$out
  if(length(outlier)>0)
    data<-data[-which(data[,col] %in% outlier),]
  boxplot(data[,col],main=paste("Box plot after removing outliers for ",col))
  
}

#Task 1.3: correlation matrix
correlations<-cor(data)
correlations
corrplot(correlations, method = "number") 


#-------------TASK 2-----------
#Task 2.1: Simple linear regression for Y=a0+a1*X1+E
model<-lm(Y~X1,data)
model #gives a0 and a1

predicted<-predict(model)
var_predicted=var(predicted)
print("Variance: ")
print(var_predicted)

residuals<-resid(model)

#Task 2.2: p-values, R-squared, F-value
summary(model)

#Task 2.3: plot the regression line
ggplot(data,aes(X1, Y)) +
  geom_point() +
  stat_smooth(method = lm) + ggtitle("Simple Linear Regression line") 
abline(model)



#Task 2.4 (a): Q-Q plot of pdf of residual against N(0,var), histogram, chi sqaure test
qqnorm(residuals, main="Q-Q plot for residuals in Linear model")
qqline(residuals, col="blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","N(0,s^2)"),  col = c("black","blue"))

hist(residuals,main="Histogram of residuals from Linear Regression")
pearson.test(residuals)

#Task 2.4 (b): Plot scatter plot for residuals
plot(predicted,residuals,main="Residuals vs Predicted")  #to see if residuals have trends

#Task 2.5: Higher order model
higher_model<-lm(Y~X1+I(X1^2),data)
higher_model
predicted_higher_model<-predict(higher_model)
var_pred_higher_model=var(predicted_higher_model)
print("Variance of the higher oder model: ")
var_pred_higher_model
summary(higher_model)
plot(data$X1,data$Y,xlab="X1",ylab="Y1")
lines(sort(data$X1), fitted(higher_model)[order(data$X1)], col = "Blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","Regression Line"),  col = c("black","blue"))

resid_higher_model<-resid(higher_model)
qqnorm(resid_higher_model, main="Q-Q plot for residuals in our model")
qqline(resid_higher_model, col="blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","N(0,s^2)"),  col = c("black","blue"))

hist(resid_higher_model,main="Histogram for the higher order model")

#-------------TASK 3-----------
#Task 3.1: Multivariate Regression
multivariate_model<-lm(Y~X1+X2+X3+X4+X5,data)
multivariate_model
pred_mv_model<-predict(multivariate_model)
var_pred_mv_model=var(pred_mv_model)
print("Variande of predicted model: ")
print(var_pred_mv_model)

#Task 3.2: P-values, R-square values, F value, correlation matrix
summary(multivariate_model)
residuals_mv_model<-residuals(multivariate_model)
qqnorm(residuals_mv_model, main="Q-Q plot for residuals in our model")
qqline(residuals_mv_model, col="blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","N(0,s^2)"),  col = c("black","blue"))

#update the model
new_mv_model<-lm(Y~X1+X3+X4+X5,data)
new_mv_model
summary(new_mv_model)
new_residuals_mv_model<-residuals(new_mv_model)
qqnorm(new_residuals_mv_model, main="Q-Q plot for residuals in our model")
qqline(new_residuals_mv_model, col="blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","N(0,s^2)"),  col = c("black","blue"))

#update again
new_mv_model2<-lm(Y~X2+X4+X5+I(X1^2),data)
new_mv_model2
summary(new_mv_model2)
new_residuals_mv_model2<-residuals(new_mv_model2)
new_pred_mv_model2<-predict(new_mv_model2)
#Task 3.3 (a): Q-Q plot of pdf of residual against N(0,var), histogram, chi sqaure test
qqnorm(new_residuals_mv_model2, main="Q-Q plot for residuals in our model")
qqline(new_residuals_mv_model2, col="blue")
legend("topleft", pch=c(1,NA), lwd=c(NA,1), legend = c("Observed Values","N(0,s^2)"),  col = c("black","blue"))
hist(new_residuals_mv_model2,main="Histogram for residuals in multivariable higher order model")
pearson.test(new_residuals_mv_model2)


#Task 3.3 (b): Plot scatter plot for residuals
plot(new_pred_mv_model2,new_residuals_mv_model2,main="Residuals vs Predicted",xlab="Residuals",ylab="Predicted")  #to see if residuals have trends













