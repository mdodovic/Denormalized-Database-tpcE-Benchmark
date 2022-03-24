# -*- coding: utf-8 -*-
"""
Created on Wed Apr  7 13:53:41 2021

@author: Matija
"""


import numpy as np
import matplotlib.pyplot as plt
import math as math

pathToNormalizedData = "C:/Users/Matija/Desktop/Fakultet/DiplomskiRad/TPC-E/tpcEBanchmark/src/time_results/normalized/";
pathToIndexedData = "C:/Users/Matija/Desktop/Fakultet/DiplomskiRad/TPC-E/tpcEBanchmark/src/time_results/indexes/";
pathToDenormalizedData = "C:/Users/Matija/Desktop/Fakultet/DiplomskiRad/TPC-E/tpcEBanchmark/src/time_results/denormalized/";

pathToResultTPCE = "C:/Users/Matija/Desktop/Fakultet/DiplomskiRad/TPC-E/tpcEBanchmark/results/time_graphs/";

def normalized(file_name):
        
    
    file_time_stamp = file_name + "_timestamp.txt"

    time_stamp_data = np.loadtxt(pathToNormalizedData + file_time_stamp, unpack=True)
    
    time_stamp_data = (time_stamp_data - time_stamp_data[0]) / 1000 / 60 / 1000000
    

    
    print("[Normalized]: ", round(time_stamp_data[-1] - time_stamp_data[0],2))
    time_stamp = np.zeros(math.ceil(time_stamp_data[-1] - time_stamp_data[0]))
    number_of_transactions = np.zeros(len(time_stamp))
    #total_time = len(time_stamp)
    
    k = 0.
    i = 0
    cnt = 0
    for time in time_stamp_data:
        if time_stamp[i] == 0:
            time_stamp[i] = k
        if k <= time and time < k + 1:
            number_of_transactions[i] += 1#e-5
        else:
#            time_stamp[i] = k
            i = i + 1
            k += 1.
            cnt += 1
        """
        if cnt == 0 or cnt == 12 or cnt == 13:
            print(number_of_transactions)
            #print(time_stamp)
            print(k)
            print(time)
        """
#    time_stamp[i] = total_time - 1
    #plt.scatter(time_stamp, number_of_transactions, s = 10)
    
    plt.plot(time_stamp, number_of_transactions)
 

def indexed(file_name):
        
    
    file_time_stamp = file_name + "_timestamp.txt"

    time_stamp_data = np.loadtxt(pathToIndexedData + file_time_stamp, unpack=True)
    
    time_stamp_data = (time_stamp_data - time_stamp_data[0]) / 1000 / 60 / 1000000
    

    
    print("[IndexedView]: ", round(time_stamp_data[-1] - time_stamp_data[0],2))
    time_stamp = np.zeros(math.ceil(time_stamp_data[-1] - time_stamp_data[0]))
    number_of_transactions = np.zeros(len(time_stamp))
    #total_time = len(time_stamp)
    
    k = 0.
    i = 0
    cnt = 0
    for time in time_stamp_data:
        if time_stamp[i] == 0:
            time_stamp[i] = k
        if k <= time and time < k + 1:
            number_of_transactions[i] += 1#e-5
        else:
#            time_stamp[i] = k
            i = i + 1
            k += 1.
            cnt += 1
        """
        if cnt == 0 or cnt == 12 or cnt == 13:
            print(number_of_transactions)
            #print(time_stamp)
            print(k)
            print(time)
        """
#    time_stamp[i] = total_time - 1
    #plt.scatter(time_stamp, number_of_transactions, s = 10)
    
    plt.plot(time_stamp, number_of_transactions)

def denormalized(file_name):
        
    
    file_time_stamp = file_name + "_timestamp.txt"

    time_stamp_data = np.loadtxt(pathToDenormalizedData + file_time_stamp, unpack=True)
    
    time_stamp_data = (time_stamp_data - time_stamp_data[0]) / 1000 / 60 / 1000000
    

    
    print("[Denormalized]: ", round(time_stamp_data[-1] - time_stamp_data[0],2))
    time_stamp = np.zeros(math.ceil(time_stamp_data[-1] - time_stamp_data[0]))
    number_of_transactions = np.zeros(len(time_stamp))
    #total_time = len(time_stamp)
    
    k = 0.
    i = 0
    cnt = 0
    for time in time_stamp_data:
        if time_stamp[i] == 0:
            time_stamp[i] = k
        if k <= time and time < k + 1:
            number_of_transactions[i] += 1#e-5
        else:
#            time_stamp[i] = k
            i = i + 1
            k += 1.
            cnt += 1
        """
        if cnt == 0 or cnt == 12 or cnt == 13:
            print(number_of_transactions)
            #print(time_stamp)
            print(k)
            print(time)
        """
#    time_stamp[i] = total_time - 1
    #plt.scatter(time_stamp, number_of_transactions, s = 10)
    
    plt.plot(time_stamp, number_of_transactions)


if __name__ == "__main__":
           
    file_name = "T2F1_read_130k"
    
    normalized(file_name)
    indexed(file_name)
    denormalized(file_name)
    file_name = "T2F1_read_130k_test2"
    denormalized(file_name)
    
    
    plt.xlabel("time [minutes]")
    plt.ylabel("Number of transactions")
    plt.title("[Read] Number of transactions per 1/6 of minute")
    plt.legend(['Normalized table', 'Indexed view', 'Denormalized table full', 'Denormalized table partial'])
    #plt.legend(['Normalized table', 'Denormalized table'])
    #plt.savefig(pathToResultTPCE + file_name + ".png", dpi = 90)
    plt.show()
    
    # Write 
    
    file_name = "T3F1_write_130k"
    
    normalized(file_name)
    indexed(file_name)
    denormalized(file_name)    
    denormalized("T3F1_write_130k_test2")
    
    
    plt.xlabel("time [minutes]")
    plt.ylabel("Number of transactions")
    plt.title("[T3F1] Number of transactions per 1/6 of minute")
    #plt.legend(['Normalized table', 'Indexed view', 'Denormalized table'])
    plt.legend(['Normalized table', 'Denormalized table'])
    #plt.savefig(pathToResultTPCE + file_name + ".png", dpi = 90)
    plt.show()
    
    
    file_name = "T8F2_write_130k"
    
    normalized(file_name)
    #indexed(file_name)
    denormalized(file_name)
    denormalized("T8F2_write_130k_test2")
    
    plt.xlabel("time [minutes]")
    plt.ylabel("Number of transactions")
    plt.title("[T8F2] Number of transactions per 1/6 of minute")
    #plt.legend(['Normalized table', 'Indexed view', 'Denormalized table'])
    plt.legend(['Normalized table', 'Denormalized table'])
    #plt.savefig(pathToResultTPCE + file_name + ".png", dpi = 90)
    plt.show()

    


    file_name = "T8F6_write_130k"
    
    normalized(file_name)
    #indexed(file_name)
    denormalized(file_name)
    denormalized("T8F6_write_130k_test2")

    plt.xlabel("time [minutes]")
    plt.ylabel("Number of transactions")
    plt.title("[T8F6] Number of transactions per 1/6 of minute")
    #plt.legend(['Normalized table', 'Indexed view', 'Denormalized table'])
    plt.legend(['Normalized table', 'Denormalized table'])
    #plt.savefig(pathToResultTPCE + file_name + ".png", dpi = 90)
    plt.show()
    
    
    
    file_name = "T3T8_all_write_130k"
    
    normalized(file_name)
    indexed("write_only_130k")
    denormalized(file_name)
    denormalized("T3T8_all_write_130k_test2")
    
    
    plt.xlabel("time [minutes]")
    plt.ylabel("Number of transactions")
    plt.title("[Write] Number of transactions per 1/6 of minute")
    plt.legend(['Normalized table', 'Indexed view', 'Denormalized table'])
    #plt.legend(['Normalized table', 'Denormalized table'])
    #plt.savefig(pathToResultTPCE + file_name + ".png", dpi = 90)
    plt.show()
    
    """
    Brisi 
    file_difference = transaction_type + "difference.txt"


    difference_data = np.loadtxt(pathToData + file_difference, unpack=True)#ovde ubacujes disk
    
    transaction_length = {}
        
    k = 0.
    i = 0
    cnt = 0
    for diff in difference_data:
        
        if diff in transaction_length:
            transaction_length[diff] += 1
        else:
            transaction_length[diff] = 1
    
    miliseconds = []
    number_of_transactions = []
    
    for l, c in transaction_length.items():
        miliseconds.append(l)
        number_of_transactions.append(c / 1e5)
        
    plt.scatter(miliseconds, number_of_transactions, s = 10)
    plt.xlabel("time [miliseconds]")
    plt.ylabel("Number of transactions [10^5]")
    plt.title("Transactions' duration distribution")
    #plt.xlim(0, 10)
    #plt.savefig(pathToResultTPCE + "F(t)_" + transaction_type + "_1m3k.png", dpi = 90)
    plt.show()
    
    """