package Interfaces;

import AbstractClasses.BodyPart;
import AbstractClasses.Clothing;
import Exceptions.BodyTypeMismatchException;

public interface PutOnItem {
    void put_on_item(Clothing item, BodyPart body_part) throws BodyTypeMismatchException;
}
