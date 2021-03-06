package pl.llp.aircasting.screens.create_account

import com.google.gson.Gson
import pl.llp.aircasting.exceptions.ErrorHandler
import pl.llp.aircasting.exceptions.InternalAPIError
import pl.llp.aircasting.exceptions.UnexpectedAPIError
import pl.llp.aircasting.lib.Settings
import pl.llp.aircasting.networking.params.CreateAccountBody
import pl.llp.aircasting.networking.params.CreateAccountParams
import pl.llp.aircasting.networking.responses.CreateAccountErrorResponse
import pl.llp.aircasting.networking.responses.UserResponse
import pl.llp.aircasting.networking.services.ApiServiceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountService(
    val mSettings: Settings,
    private val mErrorHandler: ErrorHandler,
    private val mApiServiceFactory: ApiServiceFactory
) {
    fun performCreateAccount(
        username: String, password: String, email: String, send_emails: Boolean,
        successCallback: () -> Unit,
        errorCallback: (CreateAccountErrorResponse) -> Unit
    ) {
        val apiService = mApiServiceFactory.get(emptyList())
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
                        mSettings.login(body.email, body.authentication_token)
                    }
                    successCallback()
                } else if (response.code() == 422) {
                    val errorResponse = Gson().fromJson<CreateAccountErrorResponse>(response.errorBody()?.string(), CreateAccountErrorResponse::class.java)
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
