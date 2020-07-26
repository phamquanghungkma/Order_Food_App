package com.tofukma.orderapp.CallBack

import com.tofukma.orderapp.Model.CommentModel

interface ICommentCallBack {
    fun onCommentLoadSuccess(commentList: List<CommentModel>)
    fun onCommentLoadFailed(message:String)
}