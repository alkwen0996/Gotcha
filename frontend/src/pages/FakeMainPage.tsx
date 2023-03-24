import React, { ReactElement, useState } from "react";
import { Grid } from "@mui/material";
import { Link } from "react-router-dom";
import "@styles/FakeMainPage.scss";

export default function FakeMainPage() {
  const [inputPin, setInputPin] = useState<string>("");

  const checkingMember = (e: React.ChangeEvent<HTMLInputElement>) => {
    const target = e.target.value;
    setInputPin(target);
  };

  return (
    <div style={{ minHeight: "400px", padding: "30px" }}>
      <h1 className="sorry">
        <span style={{ color: "#DFF93E" }}>❤</span>
        <span className="G">G</span>
        <span className="o">o</span>
        <span className="t">t</span>
        <span className="c">c</span>
        <span className="h">h</span>
        <span className="a">a</span>
        <span className="nn">!</span>
        <span style={{ color: "#DFF93E" }}>❤</span>
      </h1>
      <Grid container style={{ justifyContent: "center" }}>
        <Grid item sm={9} md={9} className="input-pin-num-container">
          <input
            type="text"
            placeholder="PIN번호"
            onChange={checkingMember}
            value={inputPin}
          />
        </Grid>
        {inputPin === "602602" ? (
          <Link to="/main">
            <button className="gg" type="button" style={{ cursor: "pointer" }}>
              2조만 들어와 ❤
            </button>
          </Link>
        ) : null}
      </Grid>
    </div>
  );
}
