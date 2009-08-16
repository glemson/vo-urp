f<-"result.csv"
r<-read.csv(f,comment="#",header=TRUE)
if(length(attributes(r)$names) >=2)
{
png("b_result.png")
plot(r[,1], r[,2],
,  xlab=attributes(r)$names[1],ylab=attributes(r)$names[2])
dev.off()
}
