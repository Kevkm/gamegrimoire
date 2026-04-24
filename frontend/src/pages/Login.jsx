import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import './Login.css';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8085/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data || "Login failed");
      login(data);
      window.location.href = "/dashboard";
      //navigate("/dashboard");
    } catch (err) {
      console.log("Login error:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
    <div className="auth-card">
      <div className="auth-brand">
        <span className="auth-brand-icon">📖</span>
        <h1 className="auth-brand-name">Game Grimoire</h1>
        <p className="auth-brand-sub">Sign in to your account</p>
      </div>

      {error && <div className="auth-error">{error}</div>}

      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-field">
          <label>Email</label>
          <input 
            type="email" name="email" required
            autoComplete="email"
            value={form.email} onChange={handleChange}
          />
        </div>
        <div className="auth-field">
          <label>Password</label>
          <input
            type="password" name="password" required
            autoComplete="current-password"
            value={form.password} onChange={handleChange}
          />
        </div>
        <button type="submit" className="auth-button" disabled={loading}>
          {loading ? "Signing in..." : "Sign in"}
        </button>
      </form>

      <p className="auth-switch">
        Don't have an account? <Link to="/register">Sign up</Link>
      </p>
    </div>
    </div>
  );
}