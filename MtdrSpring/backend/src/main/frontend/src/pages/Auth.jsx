import React from "react";
import { SignIn, SignUp } from "@clerk/clerk-react";

/* /sign-in */
export function SignInPage() {
  return <SignIn routing="path" path="/sign-in" />;
}

/* /sign-up */
export function SignUpPage() {
  return <SignUp routing="path" path="/sign-up" />;
}
