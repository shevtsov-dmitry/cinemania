import React, { useState, ReactElement } from 'react'
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google'
import Constants from '@/src/constants/Constants'
import useAdminPanelState from '@/src/state/adminPanelState'

const LoginWithGoogleWeb = (): ReactElement => {
    const [token, setToken] = useState<string | null>(null)

    const { showAdminPanel } = useAdminPanelState()

    const handleLoginSuccess = async (credentialResponse) => {
        const idToken = credentialResponse.credential

        try {
            const response = await fetch(
                `${Constants.VERIFICATION_SERVICE_URL}/api/v1/auth/login/google`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(idToken),
                }
            )

            if (response.ok) {
                const jwtToken = await response.text()
                setToken(jwtToken)
                localStorage.setItem('jwtToken', jwtToken)
                showAdminPanel()
                alert('Успешный вход!')
            } else {
                console.error('Login failed:', response.statusText)
                alert('Ошибка входа')
            }
        } catch (error) {
            console.error('Непредвиденная ошибка входа.', error)
            alert('Не удалось войти из за непредвиденной ошибки', error)
        }
    }

    const handleLoginFailure = (error) => {
        console.error('Login Failed:', error)
        alert('Google login failed')
    }

    return (
        <div className="flex h-fit w-fit flex-col items-center justify-center">
            <GoogleOAuthProvider clientId="your-web-client-id">
                {!token ? (
                    <GoogleLogin
                        onSuccess={handleLoginSuccess}
                        onError={handleLoginFailure}
                        buttonText="Вход через аккаунт Google"
                    />
                ) : (
                    <div>
                        <p>
                            Успешный вход по токену: {token.substring(0, 20)}...
                        </p>
                        <button
                            onClick={() => {
                                setToken(null)
                                localStorage.removeItem('jwtToken')
                            }}
                        >
                            Выйти
                        </button>
                    </div>
                )}
            </GoogleOAuthProvider>
        </div>
    )
}

export default LoginWithGoogleWeb
