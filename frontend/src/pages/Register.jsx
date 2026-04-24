import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import './Register.css';

export default function Register() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState({ email: "", username: "", password: "", confirm: "" });
    const [error, setError] = useState ("");
    const [loading, setLoading] = useState(false);

    function handleChange(e) {
        setForm(f => ({ ...f, [e.target.name]: e.target.value}));
    }
    async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirm) {
      setError("Passwords do not match");
      return;
    }
    if (form.password.length < 8) {
      setError("Password must be at least 8 characters");
      return;
    }
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8085/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: form.email,
          username: form.username,
          password: form.password,
        }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data || "Registration failed");
      login(data);
      navigate("/dashboard");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return(
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-brand">
          <span className="auth-brand-icon">📖</span>
          <h1 className="auth-brand-name">Game Grimoire</h1>
          <p className="auth-brand-sub">Create your account</p>
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
          <label>Username</label>
          <input
            type="text" name="username" required
            autoComplete="username"
            value={form.username} onChange={handleChange}
          />
        </div>
        <div className="auth-field">
          <label>Password</label>
          <input
            type="password" name="password" required
            autoComplete="new-password"
            value={form.password} onChange={handleChange}
          />
        </div>
        <div className="auth-field">
          <label>Confirm Password</label>
          <input
            type="password" name="confirm" required
            autoComplete="new-password"
            value={form.confirm} onChange={handleChange}
          />
        </div>
        <button type="submit" className="auth-btn" disabled={loading}>
          {loading ? "Creating account..." : "Create Account"}
        </button>

        <p className="auto-switch">Already have an Account?
          <Link to="/login">Sigin in</Link>
        </p>
        
          </form>
      </div>
    </div>
  );
}   