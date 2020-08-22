package com.tofukma.orderapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tofukma.orderapp.CallBack.IRecyclerItemClickListener
import com.tofukma.orderapp.Utils.Common
import com.tofukma.orderapp.EventBus.CategoryClick
import com.tofukma.orderapp.Model.CategoryModel
import com.tofukma.orderapp.R
import org.greenrobot.eventbus.EventBus

class MyCategoriesBestAdatper (internal var context: Context, internal var categoriesList: List<CategoryModel>) : RecyclerView.Adapter<MyCategoriesBestAdatper.MyViewHolder>(){

    inner class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var category_name: TextView?= null
        var category_image : ImageView?= null

        internal var listener: IRecyclerItemClickListener?= null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }

        init {
            category_name = itemView.findViewById(R.id.category_name) as TextView
            category_image = itemView.findViewById(R.id.category_image) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }

    }


    override fun getItemCount(): Int {
        return categoriesList.size

    }

//    override fun onBindViewHolder(holder: MyCategoriesBestAdatper.MyViewHolder, position: Int) {
//        Glide.with(context).load(categoriesList.get(position).image).into(holder.category_image!!)
//        holder.category_name!!.setText(categoriesList.get(position).name)
//
//        //Event
//        holder.setListener(object:IRecyclerItemClickListener{
//            override fun onItemClick(view: View, post: Int) {
//                Common.categorySelected = categoriesList.get(post)
//                EventBus.getDefault().postSticky(CategoryClick(true,categoriesList.get(post)))
//            }
//
//        })
//
//    }

    override fun getItemViewType(position: Int): Int {
        return if (categoriesList.size == 1)
            Common.DEFAULT_COLUMN_COUNT
        else
        {
            if(categoriesList.size % 2 == 0)
                Common.DEFAULT_COLUMN_COUNT
            else
                if (position > 1 && position == categoriesList.size - 1) Common.FULL_WIDTH_COLUMN
                else Common.DEFAULT_COLUMN_COUNT
        }

        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                Glide.with(context).load(categoriesList.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(categoriesList.get(position).name)

        //Event
        holder.setListener(object:IRecyclerItemClickListener{
            override fun onItemClick(view: View, post: Int) {
                Common.categorySelected = categoriesList.get(post)
                EventBus.getDefault().postSticky(CategoryClick(true,categoriesList.get(post)))
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false))
    }


}



