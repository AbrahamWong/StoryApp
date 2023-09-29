package com.minotawr.storyapp.utils

import com.minotawr.storyapp.data.local.entity.LoginInfoEntity
import com.minotawr.storyapp.data.local.entity.UserInfoEntity
import com.minotawr.storyapp.data.remote.response.LoginInfoResponse
import com.minotawr.storyapp.domain.model.LoginInfo
import com.minotawr.storyapp.domain.model.UserInfo

object AuthMapper {

    fun loginEntityToModel(entity: LoginInfoEntity?) =
        LoginInfo(
            loginResult = UserInfo(
                userId = entity?.loginResult?.userId,
                name = entity?.loginResult?.name,
                token = entity?.loginResult?.token,
            )
        )

    fun loginResponseToEntity(response: LoginInfoResponse?) =
        LoginInfoEntity(
            loginResult = UserInfoEntity(
                userId = response?.loginResult?.userId,
                name = response?.loginResult?.name,
                token = response?.loginResult?.token,
            )
        )

}