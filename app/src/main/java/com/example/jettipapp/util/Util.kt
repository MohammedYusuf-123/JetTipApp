package com.example.jettipapp.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int) =
    if (totalBill > 0 && totalBill.toString().isNotEmpty())
        (totalBill * tipPercentage) / 100.0 else 0.0

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int
): Double {
    return (calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage) + totalBill) / splitBy
}