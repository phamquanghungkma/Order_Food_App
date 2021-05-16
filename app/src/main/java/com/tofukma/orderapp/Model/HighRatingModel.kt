package com.tofukma.orderapp.Model

class HighRatingModel {
    var food_id: String ?= null
    var image: String?= null
    var menu_id: String ?= null
    var name: String ?= null


    constructor()
    constructor(food_id: String?, image: String?, menu_id: String?,name: String?){
        this.food_id = food_id
        this.image = image
        this.menu_id = menu_id
        this.name = name

    }
}