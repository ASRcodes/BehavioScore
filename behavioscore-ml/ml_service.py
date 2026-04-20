from fastapi import FastAPI
from pydantic import BaseModel
import random

app = FastAPI()

class ScoringRequest(BaseModel):
    phone: str
    behavioral_data: dict

@app.post("/predict")
async def predict_score(request: ScoringRequest):
    # Simulate behavioral analysis logic
    rent_delay = request.behavioral_data.get("rent_delay", 0)
    salary_consistency = request.behavioral_data.get("salary_consistency", 1.0)
    
    # Calculate Score (Base 300 + logic)
    base_score = 600
    if rent_delay == 0: base_score += 150
    if salary_consistency > 0.9: base_score += 100
    
    score = min(base_score + random.randint(-20, 20), 900)
    
    # Generate the "Explainable AI" Nudges
    risk_level = "LOW" if score > 750 else "MEDIUM"
    
    return {
        "results": {
            "score": score,
            "risk": risk_level,
            "default_probability": "5%" if risk_level == "LOW" else "25%"
        },
        "metrics": {
            "salary": "₹ 35,000 (Consistent)",
            "rent_status": "Paid on 5th (On-Time)",
            "savings_ratio": "32%"
        },
        "messages": {
            "lender_rec": "Strong profile. Recommended for instant approval.",
            "user_nudge": "Great job! Paying rent on time boosted your score by 150 points."
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)