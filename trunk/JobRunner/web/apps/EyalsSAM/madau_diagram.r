
r<-read.csv("madau_diagram",col.names=c("redshift","SFR"),header=TRUE)
png(paste(wd,"madau_diagram.png",sep=""))
plot(r$redshift, r$SFR, type="l",xlab="redshift",ylab="SFR")
dev.off()

