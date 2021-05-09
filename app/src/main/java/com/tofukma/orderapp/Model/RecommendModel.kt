package com.tofukma.orderapp.Model

class RecommendModel {
    var food_id: String ?= null
    var image: String?= null
    var menu_id: String ?= null
    var name: String ?= null
    var uid: String ?= null

    constructor()
    constructor(food_id: String?, image: String?, menu_id: String?,name: String?, uid: String?){
        this.food_id = food_id
        this.image = image
        this.menu_id = menu_id
        this.name = name
        this.uid = uid

    }
}