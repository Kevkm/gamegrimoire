import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => localStorage.getItem('gg_token'));
    const [user, setUser] = useState(() => {
        const stored = localStorage.getItem('gg_user');
        return stored ? JSON.parse(stored) : null;
    });

    function login(authResponse) {
        localStorage.setItem('gg_token', authResponse.token);
        localStorage.setItem('gg_user', JSON.stringify({
            id: authResponse.userid,
            username: authResponse.username,
            email: authResponse.email,
        }));
         setToken(authResponse.token);
        setUser({
            id: authResponse.userId,
            username: authResponse.username,
            email: authResponse.email,
        });
    }
    function logout() {
        localStorage.removeItem('gg_token');
        localStorage.removeItem('gg_user');
        setToken(null);
        setUser(null);
    }

    return(
        <AuthContext.Provider value={{ token, user, login, logout, isAuthenticated: !!token }}>
            {children}
        </AuthContext.Provider>
    );
}

    export function useAuth() {
        return useContext(AuthContext);
    }