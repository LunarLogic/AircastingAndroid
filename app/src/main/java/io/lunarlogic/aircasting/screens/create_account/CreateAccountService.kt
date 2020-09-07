package io.lunarlogic.aircasting.screens.create_account

import com.google.gson.Gson
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.exceptions.InternalAPIError
import io.lunarlogic.aircasting.exceptions.UnexpectedAPIError
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.params.CreateAccountBody
import io.lunarlogic.aircasting.networking.params.CreateAccountParams
import io.lunarlogic.aircasting.networking.responses.CreateAccountErrorResponse
import io.lunarlogic.aircasting.networking.responses.UserResponse
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountService(val mSettings: Settings, private val mErrorHandler: ErrorHandler) {
    fun performCreateAccount(
        username: String, password: String, email: String, send_emails: Boolean,
        successCallback: () -> Unit,
        errorCallback: (CreateAccountErrorResponse) -> Unit
    ) {
        val apiService = ApiServiceFactory.get(username, password)
        val createAccountParams = CreateAccountParams(
            username,
            password,
            email,
            send_emails
        )
        val createAccountBody = CreateAccountBody(createAccountParams)
        val call = apiService.createAccount(createAccountBody)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        mSettings.setEmail(body.email)
                        mSettings.setAuthToken(body.authentication_token)
                    }
                    successCallback()
                } else if (response.code() == 422) {
//                    println("errorek: " + response.errorBody()?.string())
                    val errorResponse = Gson().fromJson<CreateAccountErrorResponse>(response.errorBody()?.string(), CreateAccountErrorResponse::class.java )
                    println("czy ładny error? "+errorResponse.email)
                    errorCallback(errorResponse)
                } else {
                    mErrorHandler.handleAndDisplay(InternalAPIError())
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                mErrorHandler.handleAndDisplay(UnexpectedAPIError(t))
            }
        })
    }
}
