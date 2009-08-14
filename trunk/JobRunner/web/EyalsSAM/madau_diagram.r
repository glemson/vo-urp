wd<-"E:/JobRunner/Archive/gerard/0C741320F3B071B7F67911C8C7A348B9/EyalsSAM/1250262729500/"
f<-paste(wd,"madau_diagram",sep="")
r<-read.csv(f,col.names=c("redshift","SFR"),header=TRUE)
png(paste(wd,"madau_diagram.png",sep=""))
plot(r$redshift, r$SFR, type="l",xlab="redshift",ylab="SFR")
dev.off()

