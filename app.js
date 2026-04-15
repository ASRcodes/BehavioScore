// Replace with your actual Supabase Project details
const SUPABASE_URL = 'https://ldqfznhzhpxxqnwgxdmv.supabase.co';
const SUPABASE_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxkcWZ6bmh6aHB4eHFud2d4ZG12Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzEzMTk3ODAsImV4cCI6MjA4Njg5NTc4MH0.8ribXKNl0L1qeoW5wem7YrApWTM--WXxhpr_1Hd7vYA'; 

const mySupabase = supabase.createClient(SUPABASE_URL, SUPABASE_KEY);

async function requestScore() {
    const phoneInput = document.getElementById('phone');
    const statusText = document.getElementById('statusText');
    const loadingSection = document.getElementById('loadingSection');
    const resultsSection = document.getElementById('resultsSection');

    if (!phoneInput.value) return alert("Please enter a phone number!");

    // UI Feedback
    loadingSection.classList.remove('hidden');
    resultsSection.classList.add('hidden');
    statusText.innerText = "Status: Consent Sent...";

    try {
        const response = await fetch('http://localhost:8080/api/v1/lender/request-score', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phone: phoneInput.value })
        });
        
        if (!response.ok) throw new Error('Backend Error');
    } catch (error) {
        console.error("Fetch error:", error);
        statusText.innerText = "Status: Backend Connection Failed!";
        loadingSection.classList.add('hidden');
    }
}

// Subscribe to Realtime Updates
mySupabase
    .channel('lender_channel')
    .on('postgres_changes', { 
        event: 'UPDATE', 
        schema: 'public', 
        table: 'consent_requests' 
    }, (payload) => {
        const data = payload.new;
        updateUI(data);
    })
    .subscribe();
// ... (Your Supabase Client Init remains the same)

function updateUI(data) {
    const loadingSection = document.getElementById('loadingSection');
    const resultsSection = document.getElementById('resultsSection');
    const finalScoreElem = document.getElementById('finalScore');
    const riskBadge = document.getElementById('riskBadge');

    if (data.status === 'APPROVED' && !data.score) {
        const pulseBadge = document.querySelector('.status-badge.pulse');
        if (pulseBadge) pulseBadge.innerText = "BORROWER APPROVED. ANALYZING DATA...";
    }

    if (data.score) {
        loadingSection.classList.add('hidden');
        resultsSection.classList.remove('hidden');

        const analysis = data.analysis_results || {};
        
        // Dynamic Risk Styling
        const score = parseInt(data.score);
        if (score >= 750) {
            riskBadge.className = "status-badge risk-low";
            finalScoreElem.style.color = "#22c55e"; // Professional Green
        } else {
            riskBadge.className = "status-badge risk-medium";
            finalScoreElem.style.color = "#f59e0b"; // Professional Amber
        }

        // Data Injection
        document.getElementById('userNameDisplay').innerText = `Report: ${data.user_phone}`;
        finalScoreElem.innerText = score;
        riskBadge.innerText = `${analysis.results?.risk || 'LOW'} RISK PROFILE`;
        
        // --- UPDATED: Salary Sanitization & Currency Formatting ---
        let rawSalary = analysis.metrics?.salary || 30000;
        
        // Strip non-numeric characters (commas, currency symbols) if data is a string
        if (typeof rawSalary === 'string') {
            rawSalary = rawSalary.replace(/[^0-9.]/g, ''); 
        }
        
        const salaryNum = parseFloat(rawSalary);
        
        document.getElementById('salaryVal').innerText = new Intl.NumberFormat('en-IN', {
            style: 'currency', 
            currency: 'INR', 
            maximumFractionDigits: 0
        }).format(isNaN(salaryNum) ? 30000 : salaryNum);
        // -------------------------------------------------------

        document.getElementById('rentVal').innerText = analysis.metrics?.rent_status || 'CONSISTENT';
        document.getElementById('savingsVal').innerText = `${analysis.metrics?.savings_ratio || '28'}%`;
        document.getElementById('probVal').innerText = analysis.results?.default_probability || '12%';
        document.getElementById('lenderRecText').innerText = analysis.messages?.lender_rec || 'Standard approval suggested.';
    }
}