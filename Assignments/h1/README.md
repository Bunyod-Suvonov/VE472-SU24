# HW1

## Ex 1

1. Cgroups, short for control groups, are a feature of the Linux kernel that allows to allocate, limit, and isolate the resources (such as CPU, memory, disk I/O, and network bandwidth) used by a collection of processes. Cgroups enable manages resources greatly by organizing processes into hierarchical groups. This makes it possible to ensure that critical applications get the necessary resources, enhance security by isolating processes, and improve system stability and performance by preventing resource starvation. Cgroups are also widely used in containerization technologies like Docker and Kubernetes to manage the resources of containers efficiently. 

2. A process in Linux is an instance of a running program, consisting of code, data, and resources. Each process has its own unique process identifier (PID) and can perform tasks independently. Cgroups are a kernel feature that manages and limits the resources available to a collection of processes. They are not individual units of execution like processes but rather a mechanism to control and organize processes into hierarchical groups.

3. Namespaces isolate a set of similar processes from each other by providing them with separate instances of global resources. This isolation enhances security by preventing processes in one namespace from seeing or affecting processes in another namespace.

## Ex 2.

#### &emsp; Graph:
![graph.png](https://focs.ji.sjtu.edu.cn/git/ece472-24su/SuvonovBunyod522370990020-hw/src/branch/h1/graph.png)

### 1. Basic hardware profile

&emsp;a) 12th Gen Intel(R) Core(TM) i7-12700H

&emsp;b) 15981260 kB

&emsp;c) I will use `top` command to monitor both RAM and CPU usage

### 2. Determining information

#### &emsp; a) DL is most commonly late with late count of 8064705 times
#### &emsp; b) Top 3 most commonly late carriers:
* DFW delays 72276 times
* ATL delays 58137 times
* ORD delays 57754 times

#### &emsp; c) Longest delay experienced by each carrier:

* US: 1646
* WN: 883
* NW: 2601
* TW: 1086
* UA: 1437
* CO: 1187
* DL: 1439
* HP: 1309
* AA: 1521
* AS: 1140
* YV: 715
* OH: 1242
* OO: 996
* XE: 927
* TZ: 1173
* EV: 1200
* F9: 899
* FL: 1345
* HA: 1317
* MQ: 1710
* B6: 1048
* AQ: 1021
* DH: 1050
* PA (1): 1070
* PI: 1418
* EA: 1380
* PS: 569
* 9E: 1956
* ML (1): 472

### 3. Pattern identified

&emsp;The pattern identified for the dataset for year 2008 is:

&emsp;0.25038515 * DayOfWeek + 0.08558365 * DepTime - 0.07303154 * CRSDepTime - 0.01676935 * ArrTime + 0.01328631 * CRSArrTime

## Ex 3.

Please refer to the code I've uploaded in HW1 folder in the current branch