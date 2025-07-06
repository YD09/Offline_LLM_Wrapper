from fastapi import FastAPI
from pydantic import BaseModel
import subprocess
import tempfile
 
app = FastAPI()

class PromptRequest(BaseModel):
    prompt: str

@app.post("/run")
def query_llm(data: PromptRequest):
    return {"response": run_llm_inference(data.prompt)}

def run_llm_inference(prompt: str) -> str:
    with tempfile.NamedTemporaryFile(delete=False, mode="w", suffix=".txt") as f:
        f.write(prompt)
        f.flush()
        cmd = [
            "../llama.cpp/main",  # or change path if needed
            "-m", "../models/mistral-7b-instruct-v0.2.Q3_K_M.gguf",
            "-p", prompt,
        ]
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
            return result.stdout
        except Exception as e:
            return str(e)
